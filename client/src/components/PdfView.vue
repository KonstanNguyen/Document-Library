<template>
  <div class="d-grid gap-3">
    <button @click="downloadPdf" class="btn-download"> 
      Tải xuống
    </button>
    <iframe
      title="PDF"
      width="100%"
      height="1000px"
      :src="`/fakeData/${documentUrl}#toolbar=0`"
      style="border: none;"
    ></iframe>
  </div>
</template>

<script>
export default {
  props: {
    documentUrl: {
      type: String,
      default: '20191_DATN_PHAN_XUAN_PHUC_20156248.pdf',
    }
  },
  methods: {
    downloadPdf() {
      const link = document.createElement('a');
      link.href = this.documentUrl;
      link.target = '_blank';
      link.download = this.documentUrl;

      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }
}
</script>
<style scoped lang="scss">
  .btn-download{
    width: 150px;
		padding: 5px 22px;
		height: 50px;
		background-color: #1976d2;
		border: none;
		color: #fff;
		text-transform: uppercase;
		font-size: 14px;
		font-weight: 600;
		transition: all 0.3s ease 0s;
		&:hover{
			background-color: rgb(18, 33, 121);
		}
	}
</style>