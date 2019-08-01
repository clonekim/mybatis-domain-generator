<template>
  <fieldset>
    <legend>{{model.name ||'Source'}}</legend>
    <span v-if="!model.source">소스 정보가 없습니다</span>
    <div v-show="wait">
      Conneting...
    </div>

    <button @click.prevent="copyAndPaste" class="alignRight" >Copy&Paste</button>

    <pre v-show="!wait" v-bind:id="'ref_'+ format ">{{model.source}}</pre>
  </fieldset>
</template>

<script>
 export default {
   props: {
     format: {
       type: String,
       required: true
     }
   },

   data() {
     return {
       wait: false,

       model: {
         name: '',
         source: ''
       }
     }
   },

   created() {

     this.$bus.$on('onSource', (data) => {

       if(data[`${this.format}`] === true) {


         let format = null

         if(this.format === 'mapper')
           format = data.statement ? 'sql': this.format
         else
           format = this.format


         this.wait = true

         this.$http
             .post(`/source/${format}`, Object.assign({}, data,  format !== 'sql'? {statement: null} : {}))
             .then( res =>  this.model.source = res.data )
             .catch( err => {
                let { status, message, trace} = err.response.data
                alert([message, `status: ${status}`, trace].join('\n'))
             })
             .finally(() => {

               setTimeout(() => {
                 this.wait = false
               }, 600)

             })

       }else {
         Object.assign(this.model, {
           name: '',
           source: ''
         })
       }

     })
   },

   beforeDestroy() {
     this.$bus.$off('onSource')
   },

   methods: {

     copyAndPaste() {

       if(!this.model.source) {
         alert('소스내용이 없습니다')
         return
       }


       let el = document.createElement('textarea')
       document.body.append(el)
       el.value = this.model.source
       el.select()

       document.execCommand('copy')
       document.body.removeChild(el)

       alert('복사 되었습니다')
     }
   }
 }
</script>
