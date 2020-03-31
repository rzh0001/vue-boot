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

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="账户类型">
          <a-select v-decorator="['accountType',  validatorRules.accountType]" placeholder="" >
            <a-select-option value="1">对私</a-select-option>
            <a-select-option value="2">对公</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="账户名">
          <a-input placeholder="请输入账户名" v-decorator="['accountName', validatorRules.accountName]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="卡号">
          <a-input placeholder="请输入卡号" v-decorator="['cardNumber', validatorRules.cardNumber]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="银行名称">
          <a-input placeholder="请输入银行名称" v-decorator="['bankName', validatorRules.bankName]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="开户行全称">
          <a-input placeholder="请输入开户行全称" v-decorator="['branchName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备注">
          <a-input placeholder="请输入备注" v-decorator="['remark', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="开启状态">
          <a-select v-decorator="['isOpen', validatorRules.isOpen]" placeholder="" >
            <a-select-option value="0">关闭</a-select-option>
            <a-select-option value="1">打开</a-select-option>
          </a-select>
        </a-form-item>

		
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "UserBankcardModal",
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
          accountType:{rules: [{ required: true, message: '请选择账户类型!' }], initialValue: "1"},
          isOpen:{rules: [{ required: true, message: '请选择开启状态!' }], initialValue: "1"},
          accountName:{rules: [{ required: true, message: '请输入账户名!' }]},
          cardNumber:{rules: [{ required: true, message: '请输入卡号!' }]},
          bankName:{rules: [{ required: true, message: '请输入银行名称!' }]},
        },
        url: {
          add: "/df/userBankcard/add",
          edit: "/df/userBankcard/edit",
        },
      }
    },
    created () {
    },
    mounted(){
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'userId','username','accountType','accountName','cardNumber','bankName','branchName','remark','isOpen','isDefault','delFlag','version'))
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