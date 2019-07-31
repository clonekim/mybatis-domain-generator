<template>
  <div>
    <fieldset>
      <legend>Meta Data:</legend>
      <div v-show="!sqlTurnOn">
        <label for="schema">스키마 </label>
        <input type="text" v-model="param.schema" id="schema" style="margin-left: 50px;">
        <span v-if="validation.hasError('param.schema')" style="color:#ff0000">
          {{validation.firstError('param.schema')}}
        </span>
      </div>
      <div v-show="!sqlTurnOn">
        <label for="tableName">테이블명</label>
        <input type="text" v-model="param.name" id="tableName" style="margin-left: 40px;">
        <span v-if="validation.hasError('param.name')" style="color:#ff0000">
          {{validation.firstError('param.name')}}
        </span>
      </div>

      <div>
        <label for="package"> 패키지명 </label>
        <input type="text" v-model="param.package_name" id="package" style="margin-left: 35px;">
        <span v-if="validation.hasError('param.package_name')" style="color:#ff0000">
          {{validation.firstError('param.package_name')}}
        </span>
      </div>

      <div>
        <label for="pojo">POJO </label>
        <input type="text" v-model="param.pojo_name" id="pojo" style="margin-left: 50px;">
        <span v-if="validation.hasError('param.pojo_name')" style="color:#ff0000">
          {{validation.firstError('param.pojo_name')}}
        </span>
      </div>

      <div v-show="sqlTurnOn" style="margin-top:5px;">
        <label for="sql">SQL</label>
        <textarea cols="120" id="sql" v-model="param.statement" rows="10"></textarea>
        <span v-if="validation.hasError('param.statement')" style="color:#ff0000">
          {{validation.firstError('param.statement')}}
        </span>
      </div>

      <button class="primary" @click.prevent="scanTable">Scan</button>

      <span style="margin-left:35px;">
        <label>SQL직접입력<input type="checkbox" v-model="sqlTurnOn" /></label>
        <label v-show="sqlTurnOn">샘플링<input type="checkbox" v-model="param.sampling" /></label>
      </span>

    </fieldset>

    <fieldset>
      <legend>Table Meta Data:</legend>
      <div v-show="columns.length == 0">
        <h3>Scan 데이터가 없습니다</h3>
      </div>

      <div >
        <label v-show="sqlTurnOn">샘플보기<input type="checkbox" v-model="viewSample" />
          <span v-if="samples.length > 1" style="color:red">
            {{samples.length -1 }}건의 샘플데이터를 얻었습니다.
          </span>
        </label>


        <table v-show="viewSample">
          <tr v-for="( i, index)  in samples" :key="index">
            <td v-for="(j, index2) in i" :key="index2">
              {{j}}
            </td>
          </tr>
        </table>



        <table v-show="!viewSample">
          <tr style="background-color:#DFEEE">
            <td>NAME</td>
            <td>DATA TYPE</td>
            <td>SQL TYPE</td>
            <td>JAVA TYPE</td>
            <td>NULL</td>
            <td>SCALE</td>
            <td>SIZE</td>
            <td>AUTO_INCREMENT</td>
            <td>KEY</td>
          </tr>

          <tr v-for="(col, index) in columns" :key="index">
            <td>{{col.name}}</td>
            <td>{{col.data_type}}</td>
            <td>{{col.sql_type}}</td>
            <td>{{col.java_type}}</td>
            <td>{{col.null}}</td>
            <td v-if="col.scale">
              size: {{col.scale.size}}, precision: {{col.scale.precision}}
            </td>
            <td v-else>
            </td>
            <td>{{col.size}}</td>
            <td>{{col.auto_increment}}</td>
            <td><input type="checkbox" v-model="col.key"></td>
          </tr>

        </table>

        <div>
          <div>
            <label>모델사용 <input type="checkbox" v-model="param.model"></label>
            <label>감추기 <input type="checkbox" v-model="hideCol[0]"></label>
          </div>

          <div v-if="!sqlTurnOn">
            <label>컨트롤러사용 <input type="checkbox" v-model="param.controller"></label>
            <label>감추기 <input type="checkbox" v-model="hideCol[1]"></label>
          </div>

          <div>
            <label>Dao사용 <input type="checkbox" v-model="param.dao"></label>
            <label>감추기 <input type="checkbox" v-model="hideCol[2]"></label>
          </div>

          <div>
            <label>MyBatis <input type="checkbox" v-model="param.mapper"></label>
            <label>감추기 <input type="checkbox" v-model="hideCol[3]"></label>
          </div>

          <div>
            <label>MyBatis ResultMap <input type="checkbox" v-model="param.result_map"></label>
          </div>


          <div>
            <label>입력검증 <input type="checkbox" v-model="param.validation"> </label>
          </div>

          <div>
            <label>JSON <input type="checkbox" v-model="param.vue"></label>
            <label>감추기 <input type="checkbox" v-model="hideCol[4]"></label>
          </div>

          <div v-if="param.vue">
            JSON Prefix: <input type="text" v-model="param.json_prefix">
          </div>

          <button @click.prevent="makeSource">소스보기</button>
        </div>

      </div>
    </fieldset>

    <source-panel format="model"       hide-index="0"/>
    <source-panel format="controller"  hide-index="1"/>
    <source-panel format="dao"         hide-index="2"/>
    <source-panel format="mapper"      hide-index="3"/>
    <source-panel format="vue"         hide-index="4"/>

  </div>
</template>

<script>

 import SourcePanel from './SourcePanel'

 export default {
   components: {
     SourcePanel
   },

   data() {
     return {
       sqlTurnOn: false, //SQL쿼리 보기
       viewSample: false, // 실제 샘플보기
       param: {
         schema: 'UKFOS',
         name: 'USER_NOTIFICATION_T',
         package_name: 'com.koreanair.a',
         pojo_name: 'UserNotification',
         json_prefix: null,
         model: false,
         dao: false,
         mapper: false,
         controller: false,
         validation: false,
         vue: false,
         result_map: false,
         statement: null,
         sampling: false
       },
       columns: [],
       samples: [],
       hideCol: [false, false, false, false, false]
     }
   },

   created() {
     this.$bus.$on('updateHide', (index) => {
       this.hideCol[ index ] = true
     })
   },

   beforeDestroy() {
     this.$bus.$off('updateHide')
   },

   watch:{
     sqlTurnOn(v) {
       if(v === false)
         this.param.statement = null
     },

     hideCol(v) {
       this.$bus.$emit('onHide', v)
     }

   },

   methods: {
     scanTable() {
       this.$validate().then( success => {

         if(!success){
           return
         }

         let {schema, name, package_name, pojo_name, statement, sampling} = this.param

         const data = {
           schema,
           name,
           package_name,
           pojo_name,
           statement,
           sampling
         }


         this.$http
             .post('/meta', data)
             .then( res => {
               this.columns = res.data.columns
               this.samples = res.data.samples
             })
             .catch( err => {

               if(err.response.status === 400) {
                 let {message, errorCode} = err.response.data
                 alert([message, `(ErrorCode: ${errorCode})`].join('\n'))
               } else {
                 alert('에러')
               }

               this.columns = []
               this.samples = []
             })
       })
     },

     makeSource() {
       this.$bus.$emit('onSource', Object.assign({ columns: this.columns}, this.param))
     }

   },

   validators: {
     'param.schema' (value) {
       return !this.sqlTurnOn && this.$validator.value(value).required('스키마를 입력해주세요')
     },

     'param.name' (value) {
       return !this.sqlTurnOn && this.$validator.value(value).required('테이블을 입력해주세요')
     },

     'param.package_name' (value) {
       return this.$validator.value(value).required('패키지를 입력해주세요')
     },

     'param.pojo_name' (value) {
       return this.$validator.value(value).required('POJO를 입력해주세요')
     },

     'param.statement' (value) {
       return this.sqlTurnOn && this.$validator.value(value).required('쿼리를 입력해주세요')
     }
   }

 }
</script>


<style>

 table {
   border-top: 1px solid #999;
   border-left: 1px solid #999;
   border-collapse: collapse;
 }


 th, td {
   border: 1px solid #999;
   border-bottom: 1px solid #999;
   padding: 4px;
 }
</style>
