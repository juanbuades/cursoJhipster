import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PeliculaDetailComponent } from './pelicula-detail.component';

describe('Component Tests', () => {
  describe('Pelicula Management Detail Component', () => {
    let comp: PeliculaDetailComponent;
    let fixture: ComponentFixture<PeliculaDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [PeliculaDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ pelicula: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(PeliculaDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PeliculaDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load pelicula on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.pelicula).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
