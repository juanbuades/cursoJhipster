import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IEstreno, Estreno } from '../estreno.model';
import { EstrenoService } from '../service/estreno.service';
import { IPelicula } from 'app/entities/pelicula/pelicula.model';
import { PeliculaService } from 'app/entities/pelicula/service/pelicula.service';

@Component({
  selector: 'jhi-estreno-update',
  templateUrl: './estreno-update.component.html',
})
export class EstrenoUpdateComponent implements OnInit {
  isSaving = false;

  peliculasCollection: IPelicula[] = [];

  editForm = this.fb.group({
    id: [],
    fecha: [],
    lugar: [null, [Validators.minLength(4), Validators.maxLength(150)]],
    pelicula: [],
  });

  constructor(
    protected estrenoService: EstrenoService,
    protected peliculaService: PeliculaService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ estreno }) => {
      if (estreno.id === undefined) {
        const today = dayjs().startOf('day');
        estreno.fecha = today;
      }

      this.updateForm(estreno);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const estreno = this.createFromForm();
    if (estreno.id !== undefined) {
      this.subscribeToSaveResponse(this.estrenoService.update(estreno));
    } else {
      this.subscribeToSaveResponse(this.estrenoService.create(estreno));
    }
  }

  trackPeliculaById(index: number, item: IPelicula): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEstreno>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(estreno: IEstreno): void {
    this.editForm.patchValue({
      id: estreno.id,
      fecha: estreno.fecha ? estreno.fecha.format(DATE_TIME_FORMAT) : null,
      lugar: estreno.lugar,
      pelicula: estreno.pelicula,
    });

    this.peliculasCollection = this.peliculaService.addPeliculaToCollectionIfMissing(this.peliculasCollection, estreno.pelicula);
  }

  protected loadRelationshipsOptions(): void {
    this.peliculaService
      .query({ filter: 'estreno-is-null' })
      .pipe(map((res: HttpResponse<IPelicula[]>) => res.body ?? []))
      .pipe(
        map((peliculas: IPelicula[]) =>
          this.peliculaService.addPeliculaToCollectionIfMissing(peliculas, this.editForm.get('pelicula')!.value)
        )
      )
      .subscribe((peliculas: IPelicula[]) => (this.peliculasCollection = peliculas));
  }

  protected createFromForm(): IEstreno {
    return {
      ...new Estreno(),
      id: this.editForm.get(['id'])!.value,
      fecha: this.editForm.get(['fecha'])!.value ? dayjs(this.editForm.get(['fecha'])!.value, DATE_TIME_FORMAT) : undefined,
      lugar: this.editForm.get(['lugar'])!.value,
      pelicula: this.editForm.get(['pelicula'])!.value,
    };
  }
}
