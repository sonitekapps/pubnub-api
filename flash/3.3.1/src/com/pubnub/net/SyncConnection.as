package com.pubnub.net {
	import com.pubnub.operation.Operation;
	import flash.events.Event;
	/**
	 * ...
	 * @author firsoff maxim, firsoffmaxim@gmail.com, icq : 235859730
	 */
	public class SyncConnection extends ConnectionBase {
		
		private var busy:Boolean;
		
		override public function sendOperation(operation:Operation):void {
			trace('sendOperation : ' + operation.url, ready);
			super.sendOperation(operation);
			if (ready) {
				doSendOperation(operation);
			}else {
				loader.connect(operation.request);
				queue.push(operation);
			}
		}
		
		private function doSendOperation(operation:Operation):void {
			if (busy) return;
			trace('doSendOperation : ' + operation.url);
			busy = true;
			this.operation = operation;
			loader.load(operation.request);
		}
		
		private function sendNextOperation():void {
			if (queue.length > 0) {
				doSendOperation(queue.pop());
			}
		}
		
		override public function close():void {
			super.close();
			busy = false;
		}
		
		override protected function onConnect(e:Event):void {
			super.onConnect(e);
			sendNextOperation();
		}
		
		override protected function get ready():Boolean {
			return super.ready && !busy;
		}
		
		override protected function onComplete(e:URLLoaderEvent):void {
			trace('onComplete : ' + operation.url)
			
			super.onComplete(e);
			busy = false;
			sendNextOperation();
		}
	}
}