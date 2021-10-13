jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PeliculaService } from '../service/pelicula.service';
import { IPelicula, Pelicula } from '../pelicula.model';
import { IDirector } from 'app/entities/director/director.model';
import { DirectorService } from 'app/entities/director/service/director.service';

import { PeliculaUpdateComponent } from './pelicula-update.component';

describe('Component Tests', () => {
  describe('Pelicula Management Update Component', () => {
    let comp: PeliculaUpdateComponent;
    let fixture: ComponentFixture<PeliculaUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let peliculaService: PeliculaService;
    let directorService: DirectorService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PeliculaUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PeliculaUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PeliculaUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      peliculaService = TestBed.inject(PeliculaService);
      directorService = TestBed.inject(DirectorService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Director query and add missing value', () => {
        const pelicula: IPelicula = { id: 456 };
        const director: IDirector = { id: 18654 };
        pelicula.director = director;

        const directorCollection: IDirector[] = [{ id: 23186 }];
        jest.spyOn(directorService, 'query').mockReturnValue(of(new HttpResponse({ body: directorCollection })));
        const additionalDirectors = [director];
        const expectedCollection: IDirector[] = [...additionalDirectors, ...directorCollection];
        jest.spyOn(directorService, 'addDirectorToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ pelicula });
        comp.ngOnInit();

        expect(directorService.query).toHaveBeenCalled();
        expect(directorService.addDirectorToCollectionIfMissing).toHaveBeenCalledWith(directorCollection, ...additionalDirectors);
        expect(comp.directorsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const pelicula: IPelicula = { id: 456 };
        const director: IDirector = { id: 98940 };
        pelicula.director = director;

        activatedRoute.data = of({ pelicula });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(pelicula));
        expect(comp.directorsSharedCollection).toContain(director);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Pelicula>>();
        const pelicula = { id: 123 };
        jest.spyOn(peliculaService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ pelicula });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: pelicula }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(peliculaService.update).toHaveBeenCalledWith(pelicula);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Pelicula>>();
        const pelicula = new Pelicula();
        jest.spyOn(peliculaService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ pelicula });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: pelicula }));
        saveSubject.complete();

        // THEN
        expect(peliculaService.create).toHaveBeenCalledWith(pelicula);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Pelicula>>();
        const pelicula = { id: 123 };
        jest.spyOn(peliculaService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ pelicula });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(peliculaService.update).toHaveBeenCalledWith(pelicula);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackDirectorById', () => {
        it('Should return tracked Director primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackDirectorById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
