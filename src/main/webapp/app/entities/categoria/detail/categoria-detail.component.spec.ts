import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DataUtils } from 'app/core/util/data-util.service';

import { CategoriaDetailComponent } from './categoria-detail.component';

describe('Component Tests', () => {
  describe('Categoria Management Detail Component', () => {
    let comp: CategoriaDetailComponent;
    let fixture: ComponentFixture<CategoriaDetailComponent>;
    let dataUtils: DataUtils;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [CategoriaDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ categoria: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(CategoriaDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CategoriaDetailComponent);
      comp = fixture.componentInstance;
      dataUtils = TestBed.inject(DataUtils);
      jest.spyOn(window, 'open').mockImplementation(() => null);
    });

    describe('OnInit', () => {
      it('Should load categoria on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.categoria).toEqual(expect.objectContaining({ id: 123 }));
      });
    });

    describe('byteSize', () => {
      it('Should call byteSize from DataUtils', () => {
        // GIVEN
        jest.spyOn(dataUtils, 'byteSize');
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.byteSize(fakeBase64);

        // THEN
        expect(dataUtils.byteSize).toBeCalledWith(fakeBase64);
      });
    });

    describe('openFile', () => {
      it('Should call openFile from DataUtils', () => {
        const newWindow = { ...window };
        newWindow.document.write = jest.fn();
        window.open = jest.fn(() => newWindow);
        window.onload = jest.fn(() => newWindow);
        window.URL.createObjectURL = jest.fn();
        // GIVEN
        jest.spyOn(dataUtils, 'openFile');
        const fakeContentType = 'fake content type';
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.openFile(fakeBase64, fakeContentType);

        // THEN
        expect(dataUtils.openFile).toBeCalledWith(fakeBase64, fakeContentType);
      });
    });
  });
});
