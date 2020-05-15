import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {URL_BASE} from '../constants';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ImageService {

  constructor(private httpClient: HttpClient) {
  }

  uploadImage(image: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', image);

    return this.httpClient.post<string>(URL_BASE + '/images', formData).pipe(map(res => {
      console.log(res);
      return res['image_url'];
    }));
  }
}
