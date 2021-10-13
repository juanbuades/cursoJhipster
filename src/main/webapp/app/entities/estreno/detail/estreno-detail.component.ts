import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEstreno } from '../estreno.model';

@Component({
  selector: 'jhi-estreno-detail',
  templateUrl: './estreno-detail.component.html',
})
export class EstrenoDetailComponent implements OnInit {
  estreno: IEstreno | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ estreno }) => {
      this.estreno = estreno;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
