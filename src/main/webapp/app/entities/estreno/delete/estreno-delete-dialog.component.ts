import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEstreno } from '../estreno.model';
import { EstrenoService } from '../service/estreno.service';

@Component({
  templateUrl: './estreno-delete-dialog.component.html',
})
export class EstrenoDeleteDialogComponent {
  estreno?: IEstreno;

  constructor(protected estrenoService: EstrenoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.estrenoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
