import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPelicula } from '../pelicula.model';
import { PeliculaService } from '../service/pelicula.service';

@Component({
  templateUrl: './pelicula-delete-dialog.component.html',
})
export class PeliculaDeleteDialogComponent {
  pelicula?: IPelicula;

  constructor(protected peliculaService: PeliculaService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.peliculaService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
