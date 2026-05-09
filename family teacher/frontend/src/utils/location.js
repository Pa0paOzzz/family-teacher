import { CITY_DATA } from '../../../cities';

const provinceList = CITY_DATA.map(province => province.name);

function findProvince(provinceName) {
  return CITY_DATA.find(province => province.name === provinceName) || null;
}

function findCity(provinceName, cityName) {
  const province = findProvince(provinceName);
  if (!province) {
    return null;
  }
  return province.cities.find(city => city.name === cityName) || null;
}

export function getProvinceOptions() {
  return provinceList;
}

export function getCityOptions(provinceName) {
  const province = findProvince(provinceName);
  return province ? province.cities.map(city => city.name) : [];
}

export function getDistrictOptions(provinceName, cityName) {
  const city = findCity(provinceName, cityName);
  return city ? city.districts : [];
}

export function formatLocation({ province, city, district }) {
  return [province, city, district].filter(Boolean).join(' ');
}

export function buildDistrictLookup() {
  const lookup = new Map();

  CITY_DATA.forEach(province => {
    province.cities.forEach(city => {
      city.districts.forEach(district => {
        const matches = lookup.get(district) || [];
        matches.push({
          province: province.name,
          city: city.name,
          district
        });
        lookup.set(district, matches);
      });
    });
  });

  return lookup;
}

const districtLookup = buildDistrictLookup();

export function normalizeLocationFields(prefix, source = {}) {
  const provinceKey = `${prefix}Province`;
  const cityKey = `${prefix}City`;
  const districtKey = `${prefix}District`;
  const formattedKey = `${prefix}Formatted`;
  const rawValue = source[prefix] || '';

  let province = source[provinceKey] || '';
  let city = source[cityKey] || '';
  let district = source[districtKey] || '';
  let formatted = source[formattedKey] || '';

  if ((!province || !city || !district) && rawValue) {
    const parts = String(rawValue)
      .split(/\s+/)
      .map(item => item.trim())
      .filter(Boolean);

    if (parts.length >= 3) {
      [province, city, district] = parts;
    } else if (parts.length === 1) {
      const matches = districtLookup.get(parts[0]) || [];
      if (matches.length === 1) {
        province = matches[0].province;
        city = matches[0].city;
        district = matches[0].district;
      }
    }
  }

  if (!formatted) {
    formatted = formatLocation({ province, city, district });
  }

  return {
    [prefix]: formatted || rawValue,
    [provinceKey]: province,
    [cityKey]: city,
    [districtKey]: district,
    [formattedKey]: formatted
  };
}

export function buildLocationPayload(prefix, source = {}) {
  const province = source[`${prefix}Province`] || '';
  const city = source[`${prefix}City`] || '';
  const district = source[`${prefix}District`] || '';
  const formatted = formatLocation({ province, city, district });

  return {
    [prefix]: formatted,
    [`${prefix}Province`]: province,
    [`${prefix}City`]: city,
    [`${prefix}District`]: district,
    [`${prefix}Formatted`]: formatted
  };
}

export function getDisplayLocation(item, prefix) {
  return item?.[`${prefix}Formatted`] || item?.[prefix] || formatLocation({
    province: item?.[`${prefix}Province`],
    city: item?.[`${prefix}City`],
    district: item?.[`${prefix}District`]
  });
}

function normalizeLocationText(text) {
  return String(text || '')
    .replace(/[，,]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim();
}

export function matchesLocationSelection(prefix, source = {}, selected = {}) {
  const { province = '', city = '', district = '' } = selected;
  if (!province && !city && !district) {
    return true;
  }

  const normalized = normalizeLocationFields(prefix, source);
  const provinceValue = normalized[`${prefix}Province`] || '';
  const cityValue = normalized[`${prefix}City`] || '';
  const districtValue = normalized[`${prefix}District`] || '';

  const structuredMatch = (!province || provinceValue === province)
    && (!city || cityValue === city)
    && (!district || districtValue === district);

  if (structuredMatch) {
    return true;
  }

  const locationText = normalizeLocationText(
    normalized[`${prefix}Formatted`] || normalized[prefix] || source?.[`${prefix}Formatted`] || source?.[prefix]
  );

  if (!locationText) {
    return false;
  }

  return (!province || locationText.includes(province))
    && (!city || locationText.includes(city))
    && (!district || locationText.includes(district));
}
