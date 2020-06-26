<template>
  <div>
    <a-form style="max-width: 500px; margin: 40px auto 0;">
      <a-alert :closable="true" message="请向指定的账户打款后联系客服。" style="margin-bottom: 24px;" />
      <a-form-item label="充值订单号" :labelCol="{span: 5}" :wrapperCol="{span: 19}" class="stepFormText" >
        {{order.orderId}}
      </a-form-item>

      <a-form-item :labelCol="{span: 5}" :wrapperCol="{span: 19}" class="stepFormText" label="充值金额">
        <a-input-search  v-model="order.amount" :readOnly="true" @search="onCopy">
          <a-button type="primary" v-clipboard:copy="order.amount"  slot="enterButton">复制</a-button>
        </a-input-search>
      </a-form-item>

      <a-form-item label="收款人" :labelCol="{span: 5}" :wrapperCol="{span: 19}" class="stepFormText" >
        <a-input-search  v-model="order.accountName" :readOnly="true" @search="onCopy">
          <a-button type="primary" v-clipboard:copy="order.accountName"  slot="enterButton">复制</a-button>
        </a-input-search>
      </a-form-item>

      <a-form-item label="收款账户" :labelCol="{span: 5}" :wrapperCol="{span: 19}" class="stepFormText" >
        <a-input-search  v-model="order.cardNumber" :readOnly="true" @search="onCopy">
          <a-button type="primary" v-clipboard:copy="order.cardNumber"  slot="enterButton">复制</a-button>
        </a-input-search>
      </a-form-item>

      <a-form-item label="收款银行" :labelCol="{span: 5}" :wrapperCol="{span: 19}" class="stepFormText" >
        <a-input-search  v-model="order.bankName" :readOnly="true" @search="onCopy">
          <a-button type="primary" v-clipboard:copy="order.bankName"  slot="enterButton">复制</a-button>
        </a-input-search>
      </a-form-item>
      <a-form-item label="开户行" :labelCol="{span: 5}" :wrapperCol="{span: 19}" class="stepFormText" >
        <a-input-search  v-model="order.branchName" :readOnly="true" @search="onCopy">
          <a-button type="primary" v-clipboard:copy="order.branchName"  slot="enterButton">复制</a-button>
        </a-input-search>
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
      },
      onCopy: function(e) {
        this.$message.success('复制成功');
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