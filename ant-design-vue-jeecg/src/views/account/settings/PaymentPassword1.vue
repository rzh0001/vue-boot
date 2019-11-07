<template>
  <div class="account-settings-info-view">
    <a-row :gutter="16">
      <a-col :md="24" :lg="16">

        <a-form layout="vertical">

          <a-form-item label="旧密码" v-show="isSet">
            <a-input type="password" placeholder="请输入旧密码" v-decorator="[ 'oldPassword', validatorRules.oldPassword]"/>
          </a-form-item>

          <a-form-item label="新密码">
            <a-input type="password" placeholder="请输入新密码" v-decorator="[ 'password', validatorRules.password]"/>
          </a-form-item>

          <a-form-item label="确认新密码">
            <a-input type="password" @blur="handleConfirmBlur" placeholder="请确认新密码"
                     v-decorator="[ 'confirmPassword', validatorRules.confirmPassword]"/>
          </a-form-item>

          <a-form-item>
            <a-button type="primary" @click="handleOk()">提交</a-button>
            <a-button style="margin-left: 8px">保存</a-button>
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
    },
    data() {
      return {
        // cropper
        isSet: false,
        confirmDirty:false,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        preview: {},
        url: {
          get: 'sys/user/getPaymentPasswordStatus',
          update: 'sys/user/updatePaymentPassword'
        },
        validatorRules: {
          oldPassword: {
            rules: [{
              required: this.isSet, message: '请输入旧密码!'
            }]
          },
          password: {
            rules: [{
              required: true, message: '请输入新密码!'
            }, {
              validator: this.validateToNextPassword
            }]
          },
          confirmPassword: {
            rules: [{
              required: true, message: '请确认新密码!'
            }, {
              validator: this.compareToFirstPassword
            }]
          }
        }

      }
    }, created() {
      this.getPaymentPasswordStatus()
    },
    methods: {
      getPaymentPasswordStatus() {
        getAction(this.url.get).then(res => {
          if (res.success) {
            this.isSet = res.result
          }
        })
      },
      resetApiKey() {
        putAction(this.url.reset).then(res => {
          if (res.success) {
            this.getPaymentPasswordStatus()
          }
        })
      },
      handleOk() {
        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let params = Object.assign({ username: this.username }, values)
            putAction(this.url.update, params).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.close()
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.confirmLoading = false
            })
          }
        })
      },
      validateToNextPassword(rule, value, callback) {
        const form = this.form
        if (value && this.confirmDirty) {
          form.validateFields(['confirm'], { force: true })
        }
        callback()
      },
      compareToFirstPassword(rule, value, callback) {
        const form = this.form
        if (value && value !== form.getFieldValue('password')) {
          callback('两次输入的密码不一样！')
        } else {
          callback()
        }
      },
      handleConfirmBlur  (e) {
        const value = e.target.value
        this.confirmDirty = this.confirmDirty || !!value
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
      background: rgba(0, 0, 0, 0.4);
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