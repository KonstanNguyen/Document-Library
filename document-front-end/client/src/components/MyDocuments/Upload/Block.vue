<script setup>
import { defineProps } from "vue";
defineProps({
    label: {
        type: String,
        required: true,
    },
    type: {
        type: String,
        required: true,
    },
    value: {
        type: String,
        required: true,
    },
    name: {
        type: String,
        required: true,
    },
    placeholder: {
        type: String,
        required: true,
    },

})
</script>

<template>
    <div class="col-md-6 col-12 mb-3">
        <label class="form-label">{{ label }}</label>
        <input class="input-item" :type="type" :value="value" :id="id" :placeholder="placeholder" :name="name">
    </div>
</template>

<style scoped lang="scss">
    .input-item{
        background: #e9ecef;
        border: 0;
        font-size: 14px;
        width: 100%;
        padding: 10px 20px;
    }
</style>
