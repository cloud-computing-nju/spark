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
        console.log()
        if(state.wordCount[d]['name']===data['name']){
          state.wordCount[d]['value']=Number(state.wordCount[d]['value'])+Number(data['value'])
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



