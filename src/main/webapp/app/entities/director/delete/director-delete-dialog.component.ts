import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDirector } from '../director.model';
import { DirectorService } from '../service/director.service';

@Component({
  templateUrl: './director-delete-dialog.component.html',
})
export class DirectorDeleteDialogComponent {
  director?: IDirector;

  constructor(protected directorService: DirectorService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.directorService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
