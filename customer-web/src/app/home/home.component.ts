import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  services = [
    { icon: '👨‍👩‍👧', label: 'Family care & support'        },
    { icon: '🐕',     label: 'Pet sitting & walking'        },
    { icon: '🐾',     label: 'Pet medical support'          },
    { icon: '🏠',     label: 'Home visits & wellness checks'},
  ];

  reasons = [
    { icon: '🏅', label: 'Certified & experienced professionals' },
    { icon: '🕐', label: '24/7 customer support'                  },
    { icon: '👤', label: 'Personalized care plans'                },
    { icon: '💙', label: 'Trusted by thousands of families'       },
  ];
}
