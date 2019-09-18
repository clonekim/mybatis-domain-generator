<template>
  <div>
    <fieldset style="padding:20px">
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
        <textarea cols="120" id="sql" v-model="param.statement" rows="10" spellcheck="false"></textarea>
        <span v-if="validation.hasError('param.statement')" style="color:#ff0000">
          {{validation.firstError('param.statement')}}
        </span>
      </div>

      <button class="alignRight" style="width:60px;height:30px"  @click.prevent="scanTable">Scan</button>

      <span>
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
            <td><ColumnType :propSelected="col.java_type" @onColumnTypeSelected="onColumnTypeSelected(col, $event)" :condition="columTypeCondition(col)" /></td>
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
            <label>모델사용
              <a class="box" href="#ref_model">#</a>
              <input type="checkbox" v-model="param.model">
            </label>

              <label v-if="param.model"> Validation
                <input type="checkbox" v-model="param.validation">
              </label>

              <label v-if="param.model"> Underscore
                <input type="checkbox" v-model="param.underscore">
              </label>
          </div>

          <div v-if="!sqlTurnOn">
            <label>컨트롤러사용
              <a class="box" href="#ref_controller">#</a>
              <input type="checkbox" v-model="param.controller"></label>
          </div>

          <div>
            <label>Dao사용
              <a class="box" href="#ref_dao">#</a>
              <input type="checkbox" v-model="param.dao"></label>
          </div>

          <div>
            <label>MyBatis<a class="box" href="#ref_mapper">#</a><input type="checkbox" v-model="param.mapper"></label>
            <label v-if="param.mapper">ResultMap <input type="checkbox" v-model="param.result_map"></label>
          </div>


          <div>
            <label>JSON
              <a class="box" href="#ref_vue">#</a>
              <input type="checkbox" v-model="param.vue">
            </label>

            <label v-if="param.vue">
              JSON Prefix: <input type="text" v-model="param.json_prefix">
            </label>

          </div>

          <button @click.prevent="makeSource">소스생성</button>
        </div>

      </div>
    </fieldset>

    <source-panel format="model"       />
    <source-panel format="controller"  />
    <source-panel format="dao"         />
    <source-panel format="mapper"      />
    <source-panel format="vue"         />

  </div>
</template>

<script>

 import SourcePanel from './SourcePanel'
 import ColumnType  from './ColumnType'

 export default {
   components: {
     SourcePanel,
     ColumnType
   },

   data() {
     return {
       sqlTurnOn: false, //SQL쿼리 보기
       viewSample: false, // 실제 샘플보기
       param: {
         schema: 'UKFOS',
         name: null,
         package_name: 'com.koreanair',
         pojo_name: null,
         json_prefix: null,
         underscore: false,
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
       samples: []
     }
   },



   watch:{

     sqlTurnOn(v) {
       if(v === false)
         this.param.statement = null

       if(v === true) {
         this.param = Object.assign(this.param, {
           schema: null,
           name: null,
           mapper: false,
           controller: false,
           validation: false,
           vue: false,
           result_map: false
         })
       }

     }

   },

   methods: {
     columTypeCondition(col) {
        return !col.scale
     },

     onColumnTypeSelected (col, {name, value, format}) {

        if(name && value) {
            col.java_type = Object.assign(col.java_type, { name, value })
        }

        col.java_type = Object.assign(col.java_type, { format })

     },

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


         this.$emit('onProcessing', true)

         this.$bus.$emit('clearBuffer')

         this.$http
             .post('/meta', data)
             .then( res => {
               this.columns = res.data.columns
               this.samples = res.data.samples

             })
             .catch( err => {
               let {message, errorCode} = err.response.data
               alert([message || err.response.text, `(ErrorCode: ${errorCode||'Server Error(500)'})`].join('\n'))
               this.columns = []
               this.samples = []

             })
             .finally(() => {
               setTimeout(() => {
                 this.$emit('onProcessing', false)
               }, 600)

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

 .alignRight {
   position: absolute;
   right: 60px;
 }


 a.box {
   text-decoration: none;
   padding: 2px;
   text-align: center;
   text-decoration: none;
   display: inline-block;
 }
</style>
