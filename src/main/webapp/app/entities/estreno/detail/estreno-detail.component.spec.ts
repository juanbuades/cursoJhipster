import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EstrenoDetailComponent } from './estreno-detail.component';

describe('Component Tests', () => {
  describe('Estreno Management Detail Component', () => {
    let comp: EstrenoDetailComponent;
    let fixture: ComponentFixture<EstrenoDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [EstrenoDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ estreno: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(EstrenoDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(EstrenoDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load estreno on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.estreno).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
