import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CareServiceApiService, CareService } from '../services/care-service.service';

@Component({
  selector: 'app-services-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './services-page.component.html',
  styleUrl: './services-page.component.css'
})
export class ServicesPageComponent implements OnInit {

  services: CareService[] = [];
  loading = true;
  error   = '';

  // Gradient palette for the service image thumbnails (cycles by index)
  readonly thumbnailColors = [
    { from: '#bbdefb', to: '#90caf9' },
    { from: '#c8e6c9', to: '#a5d6a7' },
    { from: '#e1bee7', to: '#ce93d8' },
    { from: '#ffe0b2', to: '#ffcc80' },
  ];

  constructor(private careServiceApi: CareServiceApiService) {}

  ngOnInit(): void {
    this.careServiceApi.getAll().subscribe({
      next:  (data) => { this.services = data; this.loading = false; },
      error: ()     => { this.error = 'Could not load services. Please try again.'; this.loading = false; }
    });
  }

  thumbStyle(index: number): Record<string, string> {
    const c = this.thumbnailColors[index % this.thumbnailColors.length];
    return { background: `linear-gradient(135deg, ${c.from}, ${c.to})` };
  }
}
