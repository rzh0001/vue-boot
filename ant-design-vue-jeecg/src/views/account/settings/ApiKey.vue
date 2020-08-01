<template>
  <div class="account-settings-info-view">
    <a-row :gutter="16">
      <a-col :md="24" :lg="16">
        <div class="ant-alert ant-alert-error" style="margin-bottom: 16px;">
          系统密钥：注意！重置系统密钥后必须对应修改外部系统配置！！！
        </div>

        <a-form layout="inline">

          <a-form-item label="API KEY" :md="12" :sm="16">
            <a-input placeholder="" v-model="apiKey" :readonly="true"/>
          </a-form-item>

          <a-form-item>
            <a-popconfirm title="确定重置系统密钥吗?" @confirm="() => resetApiKey()">
              <a-button type="danger">
                重置
              </a-button>
            </a-popconfirm>
          </a-form-item>
        </a-form>
        <a-divider/>

        <div class="ant-alert ant-alert-error" style="margin-bottom: 16px;">
          服务器IP：配置后，将只允许从该IP发送接口请求
        </div>
        <a-form layout="inline">
          <a-form-item label="SERVER IP" :md="12" :sm="16">
            <a-input placeholder="" v-model="serverIp"/>
          </a-form-item>
          <a-form-item>
            <a-popconfirm title="确定修改服务器IP吗?" @confirm="() => configServerIp()">
              <a-button type="danger">
                修改
              </a-button>
            </a-popconfirm>
          </a-form-item>
        </a-form>
        <a-divider/>

        <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
          拒绝订单回调开关：默认开启。关闭后，订单拒绝时将不向对接服务器发送通知
        </div>
        <a-form layout="inline">
          <a-form-item label="开关">
            <a-select v-model="callbackSwitch" style="width: 200px" placeholder="">
              <a-select-option value="0">关闭</a-select-option>
              <a-select-option value="1">开启</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="configCallbackSwitch">
              修改
            </a-button>
          </a-form-item>
        </a-form>
      </a-col>

    </a-row>
  </div>
</template>

<script>
  import AvatarModal from './AvatarModal'
  import { getAction, putAction } from '../../../api/manage'

  export default {
    components: {
      AvatarModal
    },
    data() {
      return {
        // cropper
        apiKey: '',
        serverIp: '',
        callbackSwitch: '',
        preview: {},
        url: {
          get: 'sys/user/getUserServerConfig',
          reset: 'sys/user/resetApiKey',
          serverIp: 'sys/user/configServerIp',
          callbackSwitch: 'sys/user/configCallbackSwitch'
        }

      }
    }, created() {
      this.getUserServerConfig()
    },
    methods: {
      getUserServerConfig() {
        getAction(this.url.get).then(res => {
          if (res.success) {
            this.apiKey = res.result.apiKey
            this.serverIp = res.result.serverIp
            this.callbackSwitch = res.result.callbackSwitch + ''
            this.$message.success('刷新成功')
          }
        })
      },
      resetApiKey() {
        putAction(this.url.reset).then(res => {
          if (res.success) {
            this.getUserServerConfig()
          }
        })
      },
      configServerIp() {
        putAction(this.url.serverIp, { serverIp: this.serverIp }).then(res => {
          if (res.success) {
            this.getUserServerConfig()
          }
        })
      },
      configCallbackSwitch() {
        putAction(this.url.callbackSwitch, { callbackSwitch: this.callbackSwitch }).then(res => {
          if (res.success) {
            this.getUserServerConfig()
          }
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
  @import '~@assets/less/common.less'
</style>