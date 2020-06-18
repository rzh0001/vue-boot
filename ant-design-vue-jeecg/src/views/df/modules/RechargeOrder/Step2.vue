<template>
  <div>
    <a-form style="max-width: 500px; margin: 40px auto 0;">
      <a-alert :closable="true" message="请向指定的账户打款后联系客服。" style="margin-bottom: 24px;" />
      <a-form-item label="充值订单号" :labelCol="{span: 5}" :wrapperCol="{span: 19}" class="stepFormText" >
        {{order.orderId}}
      </a-form-item>

      <a-form-item label="充值金额" :labelCol="{span: 5}" :wrapperCol="{span: 19}" class="stepFormText" >
        {{order.amount}}
      </a-form-item>
      <a-form-item label="收款人" :labelCol="{span: 5}" :wrapperCol="{span: 19}" class="stepFormText" >
        {{order.accountName}}
      </a-form-item>
      <a-form-item label="收款账户" :labelCol="{span: 5}" :wrapperCol="{span: 19}" class="stepFormText" >
        {{order.cardNumber}}
      </a-form-item>
      <a-form-item label="开户行" :labelCol="{span: 5}" :wrapperCol="{span: 19}" class="stepFormText" >
        {{order.bankName + order.branchName}}
      </a-form-item>
      <a-form-item label="账户类型" :labelCol="{span: 5}" :wrapperCol="{span: 19}" >
        <a-select :value="order.accountType"  placeholder="" :readOnly="true" disabled>
          <a-select-option value="1">对私</a-select-option>
          <a-select-option value="2">对公</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item :wrapperCol="{span: 19, offset: 5}">
        <a-button :loading="loading" type="primary" @click="nextStep">转账成功</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
  export default {
    name: "Step2",
    props:{
      order: Object
    },
    data () {
      return {
        loading: false
      }
    },
    methods: {
      nextStep () {
        let that = this
        that.loading = true
        setTimeout(function () {
          that.$emit('nextStep')
        }, 500)
      },
      prevStep () {
        this.$emit('prevStep')
      }
    }
  }
</script>

<style lang="scss" scoped>
  .stepFormText {
    margin-bottom: 24px;

    .ant-form-item-label,
    .ant-form-item-control {
      line-height: 22px;
    }
  }

</style>