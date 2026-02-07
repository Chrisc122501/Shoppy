import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EventEmitterService {
  private orderPlacedSource = new Subject<void>();
  orderPlaced$ = this.orderPlacedSource.asObservable();

  emitOrderPlaced(): void {
    this.orderPlacedSource.next();
  }
}
