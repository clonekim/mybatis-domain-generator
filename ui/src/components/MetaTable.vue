<template>
    <div>
        <fieldset>
            <legend>Meta Data:</legend>
            <div>
                <label for="schema">스키마 </label>
                <input type="text" v-model="param.schema" id="schema" style="margin-left: 50px;">
                 <span v-if="validation.hasError('param.schema')" style="color:#ff0000">
                    {{validation.firstError('param.schema')}}
                </span>
            </div>
            <div>
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

            <button class="primary" @click.prevent="scanTable">Scan</button>
        </fieldset>

        <fieldset>
            <legend>Table Meta Data:</legend>
            <div v-show="columns.length == 0">
                <h3>Scan 데이터가 없습니다</h3>
            </div>

            <div >
                <table>
                    <tr>
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

                    <tr v-for="col in columns">
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
                    <div>
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

        <source-panel url="model"  hide-index="0"/>
        <source-panel url="controller"  hide-index="1"/>
        <source-panel url="dao"  hide-index="2"/>
        <source-panel url="mapper" hide-index="3"/>
        <source-panel url="vue"  hide-index="4"/>

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
                param: {
                    schema: '',
                    name: '',
                    package_name: 'com.koreanair.',
                    pojo_name: '',
                    json_prefix: '',
                    model: false,
                    dao: false,
                    mapper: false,
                    controller: false,
                    validation: false,
                    vue: false,
                    result_map: false,
                },
                columns: [],
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

                    let {schema, name, package_name, pojo_name} = this.param

                    const data = {
                        schema,
                        tables: [
                            {
                                name,
                                package_name,
                                pojo_name,
                            }
                        ]
                    }

                    this.$http
                        .post('/meta', data)
                        .then( res => {
                            this.columns = res.data.columns
                        })
                        .catch(() => {
                            alert('에러')
                        })
                })
            },

            makeSource() {

                const data = Object.assign({
                    columns: this.columns
                }, this.param)

                this.$bus.$emit('onSource', {data})

            }
        },

        validators: {
            'param.schema' (value) {
                return this.$validator.value(value).required('스키마를 입력해주세요')
            },

            'param.name' (value) {
                return this.$validator.value(value).required('테이블을 입력해주세요')
            },

            'param.package_name' (value) {
                return this.$validator.value(value).required('패키지를 입력해주세요')
            },

            'param.pojo_name' (value) {
                return this.$validator.value(value).required('POJO를 입력해주세요')
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
