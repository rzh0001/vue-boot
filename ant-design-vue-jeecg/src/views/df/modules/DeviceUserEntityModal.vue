<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
      
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="设备名称" >
          <a-input placeholder="设备名称"  disabled="disabled" v-model="this.deviceName"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="商户名称">
          <j-select-user-by-dep v-model="userName"></j-select-user-by-dep>
        </a-form-item>
		
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import JSelectUserByDep from '@/components/jeecgbiz/JSelectUserByDep'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "DeviceUserEntityModal",
    components:{JSelectUserByDep},
    data () {
      return {
        title:"操作",
        deviceName:"",
        userName:"",
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },

        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
        userId:{rules: [{ required: true, message: '请输入商户名称!' }]},
        },
        url: {
          add: "/df/deviceUserEntity/add",
          edit: "/df/deviceUserEntity/edit",
        },
      }
    },
    created () {
    },
    methods: {

      relationUser(record){
        this.visible = true;
        console.log(record);
        this.deviceName = record.deviceName;
      },

      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'deviceId','userId'))
		  //时间格式化
        });

      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            //时间格式化
            
            console.log(formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })



          }
        })
      },
      handleCancel () {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>

</style>