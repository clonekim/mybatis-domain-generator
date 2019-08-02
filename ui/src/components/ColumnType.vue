<template>
  <div>
   <select v-model="selected" v-if="condition">
      <option>--</option>
      <option v-for="i in options" v-bind:value="i.value">
        {{i.name}}
      </option>
   </select>

    <p v-if=" selected.indexOf('LocalDate') > 0">
       <input type="text" v-model="format" style="width:115px;" @change="inputChanged">
    </p>
  </div>
</template>


<script>
 export default {
    props: {
      propSelected: {
        type: Object,
        required: true
      },

      condition: {
        type: Boolean,
        required: false,
        default: () => true
      }
    },

    data() {
        return {
            selected: null,
            format: null,
            options: [
                {value: 'String', name: 'String'},
                {value: 'java.util.Date', name: 'Date'},
                {value: 'int', name: 'int'},
                {value: 'Integer', name: 'Integer'},
                {value: 'Long', name: 'Long'},
                {value: 'long', name: 'long'},
                {value: 'java.math.BigDecimal', name: 'Decimal'},
                {value: 'java.time.LocalDate', name: 'LocalDate'},
                {value: 'java.time.LocalDateTime', name: 'LocalDateTime'}

            ]
        }
    },

    beforeMount() {

        let option = this.options.filter(i => i.name === this.propSelected.name)

        if(option.length == 1)
            this.selected = option[0].value

    },

    watch: {
        selected(val) {


            if(val.indexOf('LocalDate') > 0) {

                switch (val) {

                    case 'java.time.LocalDate':
                    this.format = 'yyyy-MM-dd'
                    break;

                    case 'java.time.LocalDateTime':
                     this.format = 'yyyy-MM-dd HH:mm:ss'
                    break;
                }

            }else
              this.format = null


           let option = this.options.filter(i => i.value === val)

           if(option.length == 1) {
                let {name , value} = option[0]

             this.$emit('onColumnTypeSelected', {
               name,
               value,
               format: this.format
             })
           }
        }

    },

    methods: {
        inputChanged(e) {
            this.$emit('onColumnTypeSelected', {
                           format: e.target.value
                         })
        }
    }
 }
</script>
