<template>
    <div class="pdf-viewer">
      <div class="controls">
        <button @click="prevPage" :disabled="currentPage <= 1">Trang trước</button>
        <button @click="nextPage" :disabled="currentPage >= totalPages">Trang sau</button>
        <span>Trang {{ currentPage }} / {{ totalPages - 15 }}</span>
      </div>
      <div ref="pdfContainer" class="pdf-container">
        <img src="https://cdn.slidesharecdn.com/ss_thumbnails/chuong4binhquandidong-sanbangsomu-170425121551-thumbnail.jpg?width=560&fit=bounds"
            height="650" alt="">
      </div>
    </div>
  </template>
  
<script>
import * as pdfjsLib from "pdfjs-dist";
import "pdfjs-dist/build/pdf.worker";

// Cấu hình workerSrc nếu cần
pdfjsLib.GlobalWorkerOptions.workerSrc = "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.5.141/pdf.worker.min.js";

export default {
  props: {
    pdfUrl: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      pdfDoc: null,
      currentPage: 1,
      totalPages: 0,
      pdfContainer: null,
    };
  },
  methods: {
    async loadPdf() {
      try {
        const loadingTask = pdfjsLib.getDocument(this.pdfUrl);
        this.pdfDoc = await loadingTask.promise;
        this.totalPages = this.pdfDoc.numPages;
        this.renderPage(this.currentPage);
      } catch (error) {
        console.error("Không thể tải PDF:", error);
      }
    },
    async renderPage(pageNum) {
      const page = await this.pdfDoc.getPage(pageNum);

      const viewport = page.getViewport({ scale: 1.5 });
      const canvas = document.createElement("canvas");
      const context = canvas.getContext("2d");

      canvas.width = viewport.width;
      canvas.height = viewport.height;

      const renderContext = {
        canvasContext: context,
        viewport: viewport,
      };

      // Xóa nội dung cũ
      this.pdfContainer.innerHTML = "";
      this.pdfContainer.appendChild(canvas);

      // Hiển thị trang
      await page.render(renderContext).promise;
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        this.renderPage(this.currentPage);
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.renderPage(this.currentPage);
      }
    },
  },
  mounted() {
    this.pdfContainer = this.$refs.pdfContainer;
    this.loadPdf();
  },
};
</script>

  
  <style scoped>
  .pdf-viewer {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .controls {
    margin-bottom: 10px;
  }
  
  .pdf-container {
    width: 100%;
    max-width: 800px;
    height: auto;
    overflow: hidden;
    border: 1px solid #ddd;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }
  </style>
  