import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { FamilyComponent } from './family/family.component';
import { PetsComponent } from './pets/pets.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { AdminComponent } from './admin/admin.component';
import { ProfileComponent } from './profile/profile.component';
import { ServicesPageComponent } from './services-page/services-page.component';
import { PaymentComponent } from './payment/payment.component';
import { PaymentSuccessComponent } from './payment/payment-success.component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegistrationComponent },
  { path: 'family', component: FamilyComponent },
  { path: 'pets', component: PetsComponent },
  { path: 'feedback', component: FeedbackComponent },
  { path: 'admin', component: AdminComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'services', component: ServicesPageComponent },
  { path: 'payment', component: PaymentComponent },
  { path: 'payment/success', component: PaymentSuccessComponent }
];
