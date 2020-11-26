import Vue from 'vue'
import Vuex from 'vuex'
Vue.use(Vuex)
const store=new Vuex.Store({
  state:{
    wordCount:[]
  },

  mutations:{
    appendWord(state,data){
      let hasOrNot=false;
      let d;
      for(d in state.wordCount){
        if(d['name']===data['name']){
          d['value']+=data['value']
          hasOrNot=true
          break
        }
      }
      if(!hasOrNot){
        state.wordCount.push({"name":data['name'],"value":data['value']});
      }
    }
  },

  actions:{

  },

  getters:{
    getWordCount:state => state.wordCount

  }
})

export default store



