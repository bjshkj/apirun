<template>
  <div>
    <div class="name">{{ name }}</div>

    <div class="main" v-loading="isLoading">
      <ms-code-edit
        ref="codeEdit"
        v-show="!cannotPreview"
        :data.sync="content"
        height="100%"
        :read-only="true"
        :modes="Object.values(languageMap)"
        :mode="language"
        theme="monokai" />
      <div v-show="cannotPreview">当前文件不支持预览</div>
    </div>
  </div>
</template>

<script>
import MsCodeEdit from "@common/components/MsCodeEdit";

export default {
  name: "FileView",
  components: { MsCodeEdit },
  data() {
    return {
      isLoading: false,
      name: "",
      cannotPreview: false,
      languageMap: {
        py: "python",
        json: "json",
        yml: "yaml",
        txt: "text",
        md: "markdown",
      },
      language: "",
      content: "",
      editorOptions: {  // 设置代码编辑器的样式
        enableBasicAutocompletion: true,
        enableSnippets: true,
        enableLiveAutocompletion: true,
        tabSize: 2,
        fontSize: 20,
        showPrintMargin: false   // 隐藏编辑器中间的竖线（打印边距）
      }
    };
  },
  mounted() {
  },
  methods: {
    setLanguage(name) {
      const fileSuffix = name.split(".")?.slice(-1)[0];
      this.language = this.languageMap[fileSuffix] || fileSuffix;
    },
    loadData (projectId, filePath, gitRef, node) {
      if (!projectId || !filePath || !gitRef) return;

      this.name = node.name || ""
      this.setLanguage(this.name)

      this.isLoading = true;
      this.$get(`/gitLab/repoFileContent?id=${projectId}&path=${filePath}&ref=${gitRef}`, response => {
        this.content = this.b64_to_utf8(response.data.content);
        this.isLoading = false;
      })
    },
   b64_to_utf8 (str) {
    return decodeURIComponent(escape(window.atob(str)));
    }
  }
};
</script>

<style scoped>
.name {
  line-height: 1.5em;
  font-size: 18px;
  color: var(--primary_color);
}

.main {
  height: calc(100vh - 150px);
}
</style>
