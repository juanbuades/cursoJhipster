import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPelicula } from '../pelicula.model';

@Component({
  selector: 'jhi-pelicula-detail',
  templateUrl: './pelicula-detail.component.html',
})
export class PeliculaDetailComponent implements OnInit {
  pelicula: IPelicula | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pelicula }) => {
      this.pelicula = pelicula;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
