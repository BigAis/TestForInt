import {Component, OnInit} from '@angular/core';
import {AboutService} from "../services/about.service";
import {tap, catchError} from "rxjs/operators";
import {of} from "rxjs";

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.css']
})
export class AboutComponent implements OnInit {

  backVersion: string = 'Unknown';
  frontVersion: string = 'Unknown';

  constructor(private aboutService: AboutService) {
  }

  ngOnInit(): void {
    this.populateMask();
  }

  private populateMask() {
    this.aboutService.getVersion("backend").pipe(
      tap((response: any) => {
        // Handle both response.body.backend and direct response.backend
        this.backVersion = response.body?.backend || response.backend || 'Unknown';
      }),
      catchError(error => {
        console.error('Error fetching backend version:', error);
        this.backVersion = 'Error loading version';
        return of(null);
      })
    ).subscribe();
    
    this.aboutService.getVersion("frontend").pipe(
      tap((response: any) => {
        // Handle both response.body.frontend and direct response.frontend  
        this.frontVersion = response.body?.frontend || response.frontend || 'Unknown';
      }),
      catchError(error => {
        console.error('Error fetching frontend version:', error);
        this.frontVersion = 'Error loading version';
        return of(null);
      })
    ).subscribe();
  }
}