import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {SessionService} from '../../services/session.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  validateForm: FormGroup;
  success : boolean;
  // radioValue: '学生';
  //脏校验
  submitForm(): void {
    for (const i in this.validateForm.controls) {
      this.validateForm.controls[i].markAsDirty();
      this.validateForm.controls[i].updateValueAndValidity();
      
    }
    //将数据与服务器中进行比对，如果通过则进行跳转
    if(this.success){
      this.session.setCookie("user",this.validateForm.controls["id"]);
    }
    alert(this.session.getCookie("user"));
    this.validateForm.reset();
  }

  constructor(
    private fb: FormBuilder,
    private session: SessionService) {
    this.success = true;
  }

  ngOnInit(): void {
    this.validateForm = this.fb.group({
      id: [null, [
        Validators.maxLength(10),
        Validators.minLength(3),
        Validators.pattern('^[0-9]'),
        Validators.required
      ]],
      password: [null, [Validators.required]],
      // type: [null, [Validators.required]],
      remember: [true]
    });
  }

}
