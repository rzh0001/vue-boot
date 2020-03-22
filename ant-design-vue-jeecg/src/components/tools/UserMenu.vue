<template>
  <div class="user-wrapper" :class="theme">
<!--
    <span class="action">
      <a class="logout_title" target="_blank" href="http://jeecg-boot.mydoc.io">
        <a-icon type="question-circle-o"></a-icon>
      </a>
    </span>
-->
<!--
    <span class="action">
      <a-badge :count="czOrder">
        <a class="logout_title" href="javascript:;" @click="">
          <span>待处理充值订单</span>
        </a>
      </a-badge>
    </span>
    <span class="action">
      <a-badge :count="data.czCount">
        <a class="logout_title" href="javascript:;" @click="">
          <span>待处理代付订单</span>
        </a>
      </a-badge>
    </span>
-->
<!--    <header-notice class="action"/>-->
    <a-dropdown>
      <span class="action action-full ant-dropdown-link user-dropdown-menu">
        <a-avatar class="avatar" size="small" :src="getAvatar()"/>
        <span v-if="isDesktop()">欢迎您，{{ nickname() }}</span>
      </span>
      <a-menu slot="overlay" class="user-dropdown-menu-wrapper">
<!--        <a-menu-item key="0">-->
<!--          <router-link :to="{ name: 'account-center' }">-->
<!--            <a-icon type="user"/>-->
<!--            <span>个人中心</span>-->
<!--          </router-link>-->
<!--        </a-menu-item>-->
        <a-menu-item key="1">
          <router-link :to="{ name: 'account-settings-apiKey' }">
            <a-icon type="setting"/>
            <span>账户设置</span>
          </router-link>
        </a-menu-item>
        <a-menu-item key="2" @click="updatePassword">
          <a-icon type="setting"/>
          <span>密码修改</span>
        </a-menu-item>
      </a-menu>
    </a-dropdown>
    <span class="action">
      <a class="logout_title" href="javascript:;" @click="handleLogout">
        <a-icon type="logout"/>
        <span v-if="isDesktop()">&nbsp;退出登录</span>
      </a>
    </span>
    <user-password ref="userPassword"></user-password>
    <depart-select ref="departSelect" :closable="true" title="部门切换"></depart-select>
  </div>
</template>

<script>
  import HeaderNotice from './HeaderNotice'
  import UserPassword from './UserPassword'
  import DepartSelect from './DepartSelect'
  import { mapActions, mapGetters } from 'vuex'
  import { mixinDevice } from '@/utils/mixin.js'
  import { getAction,putAction } from '@/api/manage'


  export default {
    name: 'UserMenu',
    mixins: [mixinDevice],
    components: {
      HeaderNotice,
      UserPassword,
      DepartSelect
    },
    props: {
      theme: {
        type: String,
        required: false,
        default: 'dark'
      }
    },
    data () {
      return {
        loadding: false,
        url:{
          getBusinessInfo:"/sys/dashboard/businessInfo",
        },
        data: {}
      }
    },
    created() {
      this.loadData();
      this.timer();
    },
    computed:{
      czOrder () {
        return this.data.czOrder;
      }
    },

    methods: {
      ...mapActions(['Logout']),
      ...mapGetters(['nickname', 'avatar', 'userInfo']),
      getAvatar() {
        console.log('url = ' + window._CONFIG['imgDomainURL'] + '/' + this.avatar())
        return window._CONFIG['imgDomainURL'] + '/' + this.avatar()
      },
      handleLogout() {
        const that = this

        this.$confirm({
          title: '提示',
          content: '真的要注销登录吗 ?',
          onOk() {
            return that.Logout({}).then(() => {
              window.location.href = '/'
              //window.location.reload()
            }).catch(err => {
              that.$message.error({
                title: '错误',
                description: err.message
              })
            })
          },
          onCancel() {
          }
        })
      },
      updatePassword() {
        let username = this.userInfo().username
        this.$refs.userPassword.show(username)
      },
      updateCurrentDepart() {
        this.$refs.departSelect.show()
      },
      timer() {
        return setInterval(()=>{
          this.loadData()
        },60000)
      },
      loadData (){
        // 获取系统消息
        try {
          getAction(this.url.getBusinessInfo).then((res) => {
            if (res.success) {
              console.log(res)
              this.data = res.result

              if (res.result.czOrder >0 || res.result.dfOrder >0){
                this.$notification.open({
                  message: '订单提示',
                  description:
                    '您有新的订单待处理',
                  onClick: () => {
                    console.log('Notification Clicked!');
                  },
                });

                // this.$refs.audio.play();
                new Audio('http://tts.baidu.com/text2audio?cuid=baiduid&lan=zh&ctp=1&pdt=311&tex=您有新的订单，请即时处理').play()
              }
            }
          });

        } catch (err) {
        }
      }
    }
  }
</script>

<style scoped>
  .logout_title {
    color: inherit;
    text-decoration: none;
  }
</style>