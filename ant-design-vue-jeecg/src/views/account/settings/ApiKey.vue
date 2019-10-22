<template>
  <div class="account-settings-info-view">
    <a-row :gutter="16">
      <a-col :md="24" :lg="16">
        <div class="ant-alert ant-alert-error" style="margin-bottom: 16px;">
          注意！重置系统密钥后必须对应修改外部系统配置！！！
        </div>

        <a-form layout="inline">

          <a-form-item label="API_KEY" :md="12" :sm="16">
            <a-input placeholder=""  v-model="apiKey" :readonly="true"/>
          </a-form-item>

          <a-form-item>
<!--            <a-button type="primary" @click="resetApiKey">重置</a-button>-->
            <a-popconfirm title="确定重置系统密钥吗?" @confirm="() => resetApiKey()">
              <a>重置</a>
            </a-popconfirm>
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
    data () {
      return {
        // cropper
        apiKey: '',
        preview: {},
        url: {
          get: "sys/user/getApiKey",
          reset: "sys/user/resetApiKey",
        },

      }
    },created() {
      this.getApiKey()
    },
    methods: {
        getApiKey(){
          getAction(this.url.get).then(res=>{
            if(res.success){
              this.apiKey = res.result
            }
          })
        },
        resetApiKey(){
          putAction(this.url.reset).then(res=>{
            if (res.success){
              this.getApiKey()
            }
          })
        }
    }
  }
</script>

<style lang="scss" scoped>

  .avatar-upload-wrapper {
    height: 200px;
    width: 100%;
  }

  .ant-upload-preview {
    position: relative;
    margin: 0 auto;
    width: 100%;
    max-width: 180px;
    border-radius: 50%;
    box-shadow: 0 0 4px #ccc;

    .upload-icon {
      position: absolute;
      top: 0;
      right: 10px;
      font-size: 1.4rem;
      padding: 0.5rem;
      background: rgba(222, 221, 221, 0.7);
      border-radius: 50%;
      border: 1px solid rgba(0, 0, 0, 0.2);
    }
    .mask {
      opacity: 0;
      position: absolute;
      background: rgba(0,0,0,0.4);
      cursor: pointer;
      transition: opacity 0.4s;

      &:hover {
        opacity: 1;
      }

      i {
        font-size: 2rem;
        position: absolute;
        top: 50%;
        left: 50%;
        margin-left: -1rem;
        margin-top: -1rem;
        color: #d6d6d6;
      }
    }

    img, .mask {
      width: 100%;
      max-width: 180px;
      height: 100%;
      border-radius: 50%;
      overflow: hidden;
    }
  }
</style>