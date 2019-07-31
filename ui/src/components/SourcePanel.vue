<template>
  <fieldset v-show="!model.hide">
    <legend>{{model.name ||'Source'}}</legend>
    <h3 v-if="!model.source">소스 정보가 없습니다</h3>
    <pre>{{model.source}}</pre>
  </fieldset>
</template>

<script>
 export default {
   props: {
     format: {
       type: String,
       required: true
     },
     hideIndex: {
       type: String,
       required: true
     }
   },

   data() {
     return {
       model: {
         name: '',
         source: '',
         hide: false
       }
     }
   },

   created() {
     this.$bus.$on('onHide', (e) => {
       let val = e [parseInt(this.hideIndex)]
       this.model.hide = val ||false
     })

     this.$bus.$on('onSource', (data) => {



       if(data[`${this.format}`] === true) {
         console.log(data)

         let format = null

         if(this.format === 'mapper')
           format = data.statement ? 'sql': this.format
         else
           format = this.format

         this.$http
                        .post(`/source/${format}`, Object.assign({}, data,  format !== 'sql'? {statement: null} : {}))
                        .then( res => {
                          this.model.source = res.data
                        })

       }else {
         Object.assign(this.model, {
           name: '',
           source: '',
           hide: false
         })
       }

     })
   },

   beforeDestroy() {
     this.$bus.$off('onSource')
     this.$bus.$off('onHide')
   },

   methods: {
     hide() {
       this.model.hide = true
       this.$bus.$emit('updateHide', parseInt(this.hideIndex))
     }
   }
 }
</script>
