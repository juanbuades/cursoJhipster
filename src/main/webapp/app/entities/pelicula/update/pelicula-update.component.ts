import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IPelicula, Pelicula } from '../pelicula.model';
import { PeliculaService } from '../service/pelicula.service';
import { IDirector } from 'app/entities/director/director.model';
import { DirectorService } from 'app/entities/director/service/director.service';

@Component({
  selector: 'jhi-pelicula-update',
  templateUrl: './pelicula-update.component.html',
})
export class PeliculaUpdateComponent implements OnInit {
  isSaving = false;

  directorsSharedCollection: IDirector[] = [];

  editForm = this.fb.group({
    id: [],
    titulo: [null, [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
    fechaEstreno: [],
    decripcion: [null, [Validators.minLength(20), Validators.maxLength(500)]],
    enCines: [],
    director: [],
  });

  constructor(
    protected peliculaService: PeliculaService,
    protected directorService: DirectorService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pelicula }) => {
      if (pelicula.id === undefined) {
        const today = dayjs().startOf('day');
        pelicula.fechaEstreno = today;
      }

      this.updateForm(pelicula);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pelicula = this.createFromForm();
    if (pelicula.id !== undefined) {
      this.subscribeToSaveResponse(this.peliculaService.update(pelicula));
    } else {
      this.subscribeToSaveResponse(this.peliculaService.create(pelicula));
    }
  }

  trackDirectorById(index: number, item: IDirector): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPelicula>>): void {
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

  protected updateForm(pelicula: IPelicula): void {
    this.editForm.patchValue({
      id: pelicula.id,
      titulo: pelicula.titulo,
      fechaEstreno: pelicula.fechaEstreno ? pelicula.fechaEstreno.format(DATE_TIME_FORMAT) : null,
      decripcion: pelicula.decripcion,
      enCines: pelicula.enCines,
      director: pelicula.director,
    });

    this.directorsSharedCollection = this.directorService.addDirectorToCollectionIfMissing(
      this.directorsSharedCollection,
      pelicula.director
    );
  }

  protected loadRelationshipsOptions(): void {
    this.directorService
      .query()
      .pipe(map((res: HttpResponse<IDirector[]>) => res.body ?? []))
      .pipe(
        map((directors: IDirector[]) =>
          this.directorService.addDirectorToCollectionIfMissing(directors, this.editForm.get('director')!.value)
        )
      )
      .subscribe((directors: IDirector[]) => (this.directorsSharedCollection = directors));
  }

  protected createFromForm(): IPelicula {
    return {
      ...new Pelicula(),
      id: this.editForm.get(['id'])!.value,
      titulo: this.editForm.get(['titulo'])!.value,
      fechaEstreno: this.editForm.get(['fechaEstreno'])!.value
        ? dayjs(this.editForm.get(['fechaEstreno'])!.value, DATE_TIME_FORMAT)
        : undefined,
      decripcion: this.editForm.get(['decripcion'])!.value,
      enCines: this.editForm.get(['enCines'])!.value,
      director: this.editForm.get(['director'])!.value,
    };
  }
}
