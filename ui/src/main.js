import Vue from 'vue'
import App from './App.vue'
import Axios from 'axios'
import SimpleVueValidator, { Validator } from 'simple-vue-validator'

Vue.use(SimpleVueValidator)

Vue.config.productionTip = false
Vue.prototype.$http = Axios
Vue.prototype.$bus = new Vue()
Vue.prototype.$validator = Validator

new Vue({
  render: h => h(App),
}).$mount('#app')
