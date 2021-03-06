import { Injectable } from '@angular/core';
import { User } from '../models/user';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Response } from '../models/generic-response';
import { environment } from 'src/environments/environment';
import { Project } from '../models/project';
import { Course } from '../models/course';
import { GradeItem } from '../models/GradeItem';
@Injectable({
  providedIn: 'root',
})
export class AdminService {
  constructor(private http: HttpClient) {}
  getUserInformation() {
    const params = new HttpParams({
      fromObject: {
        pbl_token: String(JSON.parse(localStorage.getItem('User')).token),
      },
    });
    return this.http.get<Response<{ users: User[]; images: string[] }>>(
      `${environment.apiUrl}/api/searchAllUsers`,
      { params }
    );
  }
  getAllTeachers() {
    const params = new HttpParams({
      fromObject: {
        pbl_token: String(JSON.parse(localStorage.getItem('User')).token),
      },
    });
    return this.http.get<Response<User[]>>(
      `${environment.apiUrl}/api/searchAllTeachers`,
      { params }
    );
  }
  updateInformation(user: User) {
    const params = new HttpParams({
      fromObject: {
        pbl_token: String(JSON.parse(localStorage.getItem('User')).token),
        user: JSON.stringify(user),
      },
    });
    return this.http.put<Response<{}>>(
      `${environment.apiUrl}/api/changeInformation`,
      params
    );
  }
  addNewUser(user: User) {
    const headers = {
      headers: new HttpHeaders({
        'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
      }),
    };
    const params = {
      pbl_token: String(JSON.parse(localStorage.getItem('User')).token),
      user: JSON.stringify(user),
    };
    return this.http.post<Response<{}>>(
      `${environment.apiUrl}/api/addUser`,
      this.transformRequest(params),
      headers
    );
  }

  getAllProjects() {
    const params = new HttpParams({
      fromObject: {
        pbl_token: String(JSON.parse(localStorage.getItem('User')).token),
      },
    });
    return this.http.get<Response<{ projectList: Project[] }>>(
      `${environment.apiUrl}/api/getAllProjects`,
      { params }
    );
  }

  getAllCourses() {
    const params = new HttpParams({
      fromObject: {
        pbl_token: String(JSON.parse(localStorage.getItem('User')).token),
      },
    });
    return this.http.get<Response<{ courseList: Course[] }>>(
      `${environment.apiUrl}/api/getAllCourses`,
      { params }
    );
  }

  getAllGradeItems() {
    const params = new HttpParams({
      fromObject: {
        pbl_token: String(JSON.parse(localStorage.getItem('User')).token),
      },
    });
    return this.http.get<
      Response<{ itemList: { [p_id: number]: GradeItem[] } }>
    >(`${environment.apiUrl}/api/getAllGradeItems`, { params });
  }

  transformRequest(data) {
    let str = '';

    for (let i in data) {
      str += i + '=' + data[i] + '&';
    }

    str.substring(0, str.length - 1);

    return str;
  }
}
