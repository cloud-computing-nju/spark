import store from '../store/store'
import bus from '../utils/bus'
import GoEasy from 'goeasy'
let _ = require('lodash');

let goEasy = new GoEasy({
  host:'hangzhou.goeasy.io', //应用所在的区域地址: 【hangzhou.goeasy.io |singapore.goeasy.io】
  appkey: "BC-1e05a5514aef4b7d8b0a8fca4a0d04db", //替换为您的应用appkey
  onConnected: function() {
    console.log('连接成功！')
  },
  onDisconnected: function() {
    console.log('连接断开！')
  },
  onConnectFailed: function(error) {
    console.log('连接失败或错误！')
  }
});

export function bindPort(){
    goEasy.subscribe({
    channel: "my_channel", //替换为您自己的channel
    onMessage: function (message) {
      console.log(message.content);
      let content = message.content
      let as=_.split(content," ");
      for(let a in as){
        let w=_.split(as[a],":")
        console.log(w)
        //存入store
        store.commit('appendWord',{"name":w[0],"value":Number(w[1])})
      }
      //触发读取事件
      bus.$emit('read');
    }
  });
}


