import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthenticationService} from '../services/authentication.service';
import {Router} from '@angular/router';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  public loginForm: FormGroup;
  public error;

  constructor(private formBuilder: FormBuilder, private authenticationService: AuthenticationService, private router: Router) {
    if (this.authenticationService.currentUserValue) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  public onSubmit() {
    this.error = null
    if (this.loginForm.invalid) {
      return;
    } else {
      this.authenticationService.login(this.loginForm.controls.email.value, this.loginForm.controls.password.value)
        .pipe(first())
        .subscribe(
          data => {
            this.router.navigate(['/']);
          },
          error => {
            this.error = error;
          }
        );
    }
  }

}
