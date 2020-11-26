<template>
  <div class="hello">
    <div>
      <wordcloud
        :data="defaultWords"
        nameKey="name"
        valueKey="value"
        :color="myColors"
        :showTooltip="false"
        :wordClick="wordClickHandler">
      </wordcloud>
    </div>
  </div>
</template>
<script>
import wordcloud from 'vue-wordcloud'
import {bindListener,bindPort} from '../utils/socket'
import bus from '../utils/bus'

export default {
  name: 'Server',
  components: {
    wordcloud
  },
  methods: {
    wordClickHandler (name, value, vm) {
      console.log('wordClickHandler', name, value, vm)
    }
  },
  mounted() {
    console.log("mounted")
    bindPort()
    bus.$on('read',()=>{
      //更新数据
      console.log(this.$store.state.wordCount)
      this.defaultWords=this.$store.state.wordCount
    })

  },
  data () {
    return {
      myColors: ['#1f77b4'],
      defaultWords: [{
        'name': 'Cat',
        'value': 26
      },
        {
          'name': 'Dog',
          'value': 20
        }]
    }
  }
}
</script>
