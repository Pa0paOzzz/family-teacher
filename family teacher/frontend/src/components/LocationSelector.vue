<template>
  <div class="location-selector">
    <el-select
      :model-value="province"
      placeholder="选择省份"
      clearable
      class="location-select"
      @change="handleProvinceChange"
    >
      <el-option
        v-for="item in provinceOptions"
        :key="item"
        :label="item"
        :value="item"
      />
    </el-select>

    <el-select
      :model-value="city"
      placeholder="选择城市"
      clearable
      class="location-select"
      :disabled="!province"
      @change="handleCityChange"
    >
      <el-option
        v-for="item in cityOptions"
        :key="item"
        :label="item"
        :value="item"
      />
    </el-select>

    <el-select
      :model-value="district"
      placeholder="选择区县"
      clearable
      class="location-select"
      :disabled="!city"
      @change="handleDistrictChange"
    >
      <el-option
        v-for="item in districtOptions"
        :key="item"
        :label="item"
        :value="item"
      />
    </el-select>
  </div>
</template>

<script>
import { getProvinceOptions, getCityOptions, getDistrictOptions, formatLocation } from '../utils/location';

export default {
  name: 'LocationSelector',
  props: {
    province: {
      type: String,
      default: ''
    },
    city: {
      type: String,
      default: ''
    },
    district: {
      type: String,
      default: ''
    }
  },
  emits: ['update'],
  computed: {
    provinceOptions() {
      return getProvinceOptions();
    },
    cityOptions() {
      return getCityOptions(this.province);
    },
    districtOptions() {
      return getDistrictOptions(this.province, this.city);
    }
  },
  methods: {
    emitUpdate(province, city, district) {
      this.$emit('update', {
        province,
        city,
        district,
        formatted: formatLocation({ province, city, district })
      });
    },
    handleProvinceChange(value) {
      this.emitUpdate(value || '', '', '');
    },
    handleCityChange(value) {
      this.emitUpdate(this.province || '', value || '', '');
    },
    handleDistrictChange(value) {
      this.emitUpdate(this.province || '', this.city || '', value || '');
    }
  }
};
</script>

<style scoped>
.location-selector {
  display: flex;
  gap: 12px;
  width: 100%;
}

.location-select {
  flex: 1;
}
</style>
