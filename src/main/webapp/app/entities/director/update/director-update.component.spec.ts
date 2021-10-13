jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { DirectorService } from '../service/director.service';
import { IDirector, Director } from '../director.model';

import { DirectorUpdateComponent } from './director-update.component';

describe('Component Tests', () => {
  describe('Director Management Update Component', () => {
    let comp: DirectorUpdateComponent;
    let fixture: ComponentFixture<DirectorUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let directorService: DirectorService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [DirectorUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(DirectorUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DirectorUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      directorService = TestBed.inject(DirectorService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const director: IDirector = { id: 456 };

        activatedRoute.data = of({ director });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(director));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Director>>();
        const director = { id: 123 };
        jest.spyOn(directorService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ director });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: director }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(directorService.update).toHaveBeenCalledWith(director);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Director>>();
        const director = new Director();
        jest.spyOn(directorService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ director });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: director }));
        saveSubject.complete();

        // THEN
        expect(directorService.create).toHaveBeenCalledWith(director);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Director>>();
        const director = { id: 123 };
        jest.spyOn(directorService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ director });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(directorService.update).toHaveBeenCalledWith(director);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
