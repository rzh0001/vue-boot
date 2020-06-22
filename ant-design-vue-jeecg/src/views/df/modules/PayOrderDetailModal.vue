<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk" okText="已支付"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单号" >
          <a-input placeholder="" v-decorator="['orderId', validatorRules.orderId ]" :readOnly="true"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="外部订单号">
          <a-input placeholder="" v-decorator="['outerOrderId', validatorRules.outerOrderId ]" :readOnly="true"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="用户" >
          <a-input placeholder="" v-decorator="['userName', {}]" :readOnly="true"/>
        </a-form-item>
<!--        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="商户编号">-->
<!--          <a-input placeholder="请输入商户编号" v-decorator="['merchantId', validatorRules.merchantId ]" />-->
<!--        </a-form-item>-->
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单金额">
          <a-input-number v-decorator="[ 'amount', validatorRules.amount]"  disabled/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="交易手续费"  v-show="false">
          <a-input-number v-decorator="[ 'transactionFee', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="固定手续费" v-show="false">
          <a-input-number v-decorator="[ 'fixedFee', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单总手续费" v-show="false">
          <a-input-number v-decorator="[ 'orderFee', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="通道">
          <a-select v-decorator="['channel', {}]" placeholder="" >
            <a-select-option value="bank">银行转账</a-select-option>
            <a-select-option value="alipay">支付宝</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="账户类型">
          <a-select v-decorator="['accountType', {}]" placeholder="" >
            <a-select-option value="1">对私</a-select-option>
            <a-select-option value="2">对公</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="账户名">
          <a-input placeholder="" v-decorator="['accountName', validatorRules.accountName]" :readOnly="true"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="卡号">
          <a-input placeholder="" v-decorator="['cardNumber', {}]"  :readOnly="true"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="银行名称">
          <a-input placeholder="" v-decorator="['bankName', {}]" :readOnly="true" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开户行全称">
          <a-input placeholder="" v-decorator="['branchName', {}]" :readOnly="true"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
          <a-input placeholder="" v-decorator="['remark', {}]" :readOnly="true"/>
        </a-form-item>
<!--        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="操作IP">-->
<!--          <a-input placeholder="请输入操作IP" v-decorator="['ip', {}]" />-->
<!--        </a-form-item>-->

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "PayOrderModal",
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
          amount:{rules: [{ required: true, message: '请输入金额!' }]},
          channel:{rules: [{ required: true, message: '请选择通道!' }]},
          accountName:{rules: [{ required: true, message: '请输入账户名!' }]},
          cardNumber:{rules: [{ required: true, message: '请输入卡号或支付宝帐号!' }]},
        },
        url: {

        },
      }
    },
    created () {
    },
    methods: {
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'orderId','outerOrderId','userId','userName','userRealname',
            'amount','transactionFee','fixedFee','orderFee','channel','accountType','accountName',
            'cardNumber','bankName','branchName','status','remark','agentId','agentUsername','agentRealname',
            'salesmanId','salesmanUsername','salesmanRealname','ip'))
        });

      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        // this.$emit("handleApproval", {id: this.model.id, status: '2'})

        console.log(this.model)
        // this.$parent.handleApproval({id: this.model.id, status: '2'})
        this.$emit("handleApproval", {id: this.model.id, status: '2'})
        this.close()
      },
      handleCancel () {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>

</style>