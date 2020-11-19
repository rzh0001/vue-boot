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
      
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="设备名称">
          <a-input placeholder="请输入设备名称" v-decorator="['deviceName', validatorRules.deviceName ]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="设备编码">
          <a-input placeholder="请输入设备编码" v-decorator="['deviceCode', validatorRules.deviceCode ]"
                   :readOnly="!!model.deviceCode"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="秘钥">
          <a-input placeholder="请输入秘钥" v-decorator="['apiKey', validatorRules.key ]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="限额">
          <a-input placeholder="请输入限额" v-decorator="['limitMoney', {}]" />
        </a-form-item>
       <!-- <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="分组编码">
          <a-input placeholder="请输入分组编码" v-decorator="['groupingCode', {}]" />
        </a-form-item>-->
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
          <select v-decorator="[ 'status', validatorRules.status]">
            <option value="1">开启</option>
            <option value="2">禁用</option>
          </select>
        </a-form-item>
      <!--  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="清零时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'clearedTime', {}]" />
        </a-form-item>-->
		
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"
  import {disabledAuthFilter} from "@/utils/authFilter";

  export default {
    name: "DeviceInfoEntityModal",
    data () {
      return {
        title:"操作",
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
        deviceName:{rules: [{ required: true, message: '请输入设备名称!' }]},
        deviceCode:{rules: [{ required: true, message: '请输入设备编码!' }]},
          apiKey:{rules: [{ required: true, message: '请输入秘钥!' }]},
        status:{rules: [{ required: true, message: '请输入状态：1：正常；2：禁用!' }]},
        },
        url: {
          add: "/df/deviceInfoEntity/add",
          edit: "/df/deviceInfoEntity/edit",
        },
      }
    },
    created () {
    },
    methods: {
      isDisabledAuth(code) {
        return disabledAuthFilter(code)
      },
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'deviceName','deviceCode','apiKey','limitMoney','balance','groupingCode','status'))
		  //时间格式化
          this.form.setFieldsValue({clearedTime:this.model.clearedTime?moment(this.model.clearedTime):null})
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
            formData.clearedTime = formData.clearedTime?formData.clearedTime.format('YYYY-MM-DD HH:mm:ss'):null;
            
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