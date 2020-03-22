<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-form :form="form">
      <a-form-item>
        <a-alert :message="'请向匹配的银行卡打款！'" type="info" showIcon/>
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单号">
        <a-input placeholder="" v-decorator="['orderId', {} ]"/>
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="账户类型">
        <a-select v-decorator="['accountType', {}]" placeholder="">
          <a-select-option value="1">对私</a-select-option>
          <a-select-option value="2">对公</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="账户名">
        <a-input placeholder="" v-decorator="['accountName', {}]"/>
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="卡号">
        <a-input placeholder="" v-decorator="['cardNumber', {}]"/>
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="银行名称">
        <a-input placeholder="" v-decorator="['bankName', {}]"/>
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开户行全称">
        <a-input placeholder="" v-decorator="['branchName', {}]"/>
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="充值金额">
        <a-input-number v-decorator="[ 'amount', {}]" :min="0"/>
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="付款人">
        <a-input placeholder="" v-decorator="['payer', {}]"/>
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
        <a-input placeholder="" v-decorator="['remark', {}]"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from 'moment'
  import { getAction } from '../../../api/manage'

  export default {
    name: 'RechargeOrderResultModal',
    data() {
      return {
        title: '操作',
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },

        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          orderId: { rules: [{ required: false, message: '请输入订单号!' }] },
          outerOrderId: { rules: [{ required: false, message: '请输入外部订单号!' }] },
          merchantId: { rules: [{ required: false, message: '请输入商户编号!' }] },
          status: { rules: [{ required: false, message: '请输入订单状态：0-已保存;1-已打款,待审核;2-已确认;3-审核拒绝!' }] },
          bankcardId: { rules: [{ required: true, message: '请输入银行卡ID!' }] },
          amount: { rules: [{ required: true, message: '请输入充值金额!' }] }
        },
        url: {
          add: '/df/rechargeOrder/add',
          edit: '/df/rechargeOrder/edit',
          getBankcard: '/df/rechargeOrder/getBankcard'
        },
        bankcard: {},
        text:"",
      }
    },
    created() {
    },
    methods: {
      add() {
        this.edit()
      },
      getBankcard() {
        getAction(this.url.getBankcard, {}).then((res) => {
          if (res.success) {
            this.bankcard.bankcardId = res.result.id
            this.bankcard.accountType = res.result.accountType
            this.bankcard.accountName = res.result.accountName
            this.bankcard.cardNumber = res.result.cardNumber
            this.bankcard.accountType = res.result.accountType
            this.bankcard.branchName = res.result.branchName
            this.bankcard.bankName = res.result.bankName
            this.edit(this.bankcard)
          } else {
            this.$message.error('操作失败，' + res.message)
          }
        })
      },
      edit(record) {
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'orderId', 'outerOrderId', 'userId', 'userName', 'userRealname', 'merchantId', 'amount', 'status', 'bankcardId', 'accountType', 'accountName', 'cardNumber', 'bankName', 'branchName', 'remark', 'agentId', 'agentUsername', 'agentRealname', 'salesmanId', 'salesmanUsername', 'salesmanRealname', 'ip'))
          //时间格式化
          this.form.setFieldsValue({ successTime: this.model.successTime ? moment(this.model.successTime) : null })
        })
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      handleOk() {
        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let httpurl = ''
            let method = ''
            if (!this.model.id) {
              httpurl += this.url.add
              method = 'post'
            } else {
              httpurl += this.url.edit
              method = 'put'
            }
            let formData = Object.assign(this.model, values)
            //时间格式化
            formData.successTime = formData.successTime ? formData.successTime.format('YYYY-MM-DD HH:mm:ss') : null

            console.log(formData)
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                // that.$emit('ok');
                that.add(res.result)
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.confirmLoading = false
              // that.close();
            })


          }
        })
      },
      handleCancel() {
        this.close()
      }


    }
  }
</script>

<style lang="less" scoped>

</style>