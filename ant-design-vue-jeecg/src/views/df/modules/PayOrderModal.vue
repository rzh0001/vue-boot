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
        <a-form-item>
          <a-alert :message="'剩余代付额度：' + avaliableAmount" type="info" showIcon/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单号" v-show="false">
          <a-input placeholder="请输入订单号" v-decorator="['orderId', validatorRules.orderId ]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="外部订单号" v-show="false">
          <a-input placeholder="请输入外部订单号" v-decorator="['outerOrderId', validatorRules.outerOrderId ]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="用户" v-show="false">
          <a-input placeholder="请输入用户" v-decorator="['userName', {}]" />
        </a-form-item>
<!--        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="商户编号">-->
<!--          <a-input placeholder="请输入商户编号" v-decorator="['merchantId', validatorRules.merchantId ]" />-->
<!--        </a-form-item>-->
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单金额">
          <a-input-number v-decorator="[ 'amount', validatorRules.amount]"  min="0"/>
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
          <a-select v-decorator="['channel', validatorRules.channel]" placeholder="" >
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
          <a-input placeholder="请输入账户名" v-decorator="['accountName', validatorRules.accountName]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="卡号">
          <a-input placeholder="请输入卡号或支付宝帐号" v-decorator="['cardNumber', validatorRules.cardNumber]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="银行名称">
          <a-input placeholder="请输入银行名称" v-decorator="['bankName', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开户行全称">
          <a-input placeholder="请输入开户行全称" v-decorator="['branchName', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单状态：0-待处理;1-已接单;2-已打款;3-审核拒绝"  v-show="false">
          <a-input placeholder="请输入订单状态：0-待处理;1-已接单;2-已打款;3-审核拒绝" v-decorator="['status', validatorRules.status ]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
          <a-input placeholder="请输入备注" v-decorator="['remark', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="成功时间"  v-show="false">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'successTime', {}]" />
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
          add: "/df/payOrder/add",
          edit: "/df/payOrder/edit",
          getMemberAvailableAmount: '/sys/userAmountEntity/getMemberAvailableAmount'
        },
        avaliableAmount: 0,
      }
    },
    created () {
    },
    methods: {
      add () {
        this.getMemberAvailableAmount();
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'orderId','outerOrderId','userId','userName','userRealname','merchantId','amount','transactionFee','fixedFee','orderFee','channel','accountType','accountName','cardNumber','bankName','branchName','status','remark','agentId','agentUsername','agentRealname','salesmanId','salesmanUsername','salesmanRealname','ip'))
          //时间格式化
          this.form.setFieldsValue({successTime:this.model.successTime?moment(this.model.successTime):null})
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
            formData.successTime = formData.successTime?formData.successTime.format('YYYY-MM-DD HH:mm:ss'):null;

            console.log(formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
                that.close();
              }else{
                that.$message.error(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              // that.close();
            })



          }
        })
      },
      handleCancel () {
        this.close()
      },
      getMemberAvailableAmount() {
        getAction(this.url.getMemberAvailableAmount).then((res) => {
          if (res.success) {
            this.avaliableAmount = res.result
          } else {
            console.log(res.message)
            this.$message.error(res.message)
          }
        })
      },


    }
  }
</script>

<style lang="less" scoped>

</style>