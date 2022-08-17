require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "${SOC_FAMILY}-flashtools-native dtc-native tegra-flashvars lz4-native"

BCT_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT}"
BCT_OVERRIDE_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT_OVERRIDE}"
BOARD_CFG ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${NVIDIA_BOARD_CFG}"
PARTITION_FILE ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${PARTITION_LAYOUT_TEMPLATE}"
SMD_CFG ?= "${S}/bootloader/smd_info.cfg"
CBOOTOPTION_FILE ?= ""
ODMFUSE_FILE ?= ""

BOOTBINS:tegra194 = "\
    adsp-fw.bin \
    bpmp-2_t194.bin \
    camera-rtcpu-t194-rce.img \
    dram-ecc-t194.bin \
    eks.img \
    mb1_t194_prod.bin \
    nvdisp-init.bin \
    nvtboot_applet_t194.bin \
    nvtboot_t194.bin \
    preboot_c10_prod_cr.bin \
    mce_c10_prod_cr.bin \
    mts_c10_prod_cr.bin \
    nvtboot_cpu_t194.bin \
    nvtboot_recovery_t194.bin \
    nvtboot_recovery_cpu_t194.bin \
    spe_t194.bin \
    warmboot_t194_prod.bin \
"

BOOTBINS:tegra234 = "\
    adsp-fw.bin \
    applet_t234.bin \
    ${BPF_FILE} \
    camera-rtcpu-t234-rce.img \
    eks.img \
    mb1_t234_prod.bin \
    mb2_t234.bin \
    preboot_c10_prod_cr.bin \
    mce_c10_prod_cr.bin \
    mts_c10_prod_cr.bin \
    nvtboot_cpurf_t234.bin \
    spe_t234.bin \
    psc_bl1_t234_prod.bin \
    pscfw_t234_prod.bin \
    mce_flash_o10_cr_prod.bin \
    sc7_t234_prod.bin \
    dce.bin \
    psc_rf_t234_prod.bin \
    nvdec_t234_prod.fw \
    xusb_t234_prod.bin \
    tegrabl_carveout_id.h \
    pinctrl-tegra.h \
    tegra234-gpio.h \
    gpio.h \
    readinfo_t234_min_prod.xml \
    camera-rtcpu-sce.img \
"

BOOTBINS_MACHINE_SPECIFIC:tegra194 = ""
BOOTBINS_MACHINE_SPECIFIC:tegra234 = ""

do_compile() {
    :
}

do_compile:append:tegra194() {
    ${STAGING_BINDIR_NATIVE}/tegra194-flash/nv_smd_generator ${SMD_CFG} ${B}/slot_metadata.bin
    if [ -n "${CBOOTOPTION_FILE}" ]; then
        dtc -I dts -O dtb -o ${B}/cbo.dtb ${CBOOTOPTION_FILE}
    fi
}

do_compile:append:tegra234() {
    ${STAGING_BINDIR_NATIVE}/tegra234-flash/nv_smd_generator ${SMD_CFG} ${B}/slot_metadata.bin
    if [ -n "${CBOOTOPTION_FILE}" ]; then
        dtc -I dts -O dtb -o ${B}/cbo.dtb ${CBOOTOPTION_FILE}
    fi
}

do_install() {
    install -d ${D}${datadir}/tegraflash
    install -m 0644 ${S}/nv_tegra/bsp_version ${D}${datadir}/tegraflash/
    for f in ${BOOTBINS}; do
        install -m 0644 ${S}/bootloader/$f ${D}${datadir}/tegraflash
    done
    for f in ${BOOTBINS_MACHINE_SPECIFIC}; do
        install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/$f ${D}${datadir}/tegraflash
    done
    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
    [ -z "${ODMFUSE_FILE}" ] || install -m 0644 ${ODMFUSE_FILE} ${D}${datadir}/tegraflash/odmfuse_pkc_${MACHINE}.xml
}

do_install:append:jetson-xavier-nx-devkit-tx2-nx() {
    # XXX only 16GiB eMMC on tx2-nx
    sed -i -e's,num_sectors="61071360",num_sectors="30777344",' ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
}

do_install:append:tegra194() {
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${MACHINE}.cfg
    install -m 0644 ${B}/slot_metadata.bin ${D}${datadir}/tegraflash/
    install -m 0644 ${BCT_OVERRIDE_TEMPLATE} ${D}${datadir}/tegraflash/${MACHINE}-override.cfg
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra19* ${D}${datadir}/tegraflash/
    for f in ${S}/bootloader/${NVIDIA_BOARD}/tegra194-*-bpmp-*.dtb; do
        install -m 0644 $f ${D}${datadir}/tegraflash/
        compressedfile=$(basename "$f" .dtb)_lz4.dtb
        lz4c -f $f ${D}${datadir}/tegraflash/$compressedfile
        chmod 0644 ${D}${datadir}/tegraflash/$compressedfile
    done
    install -m 0644 ${S}/bootloader/xusb_sil_rel_fw ${D}${datadir}/tegraflash/
    if [ -n "${CBOOTOPTION_FILE}" ]; then
        install -m 0644 ${B}/cbo.dtb ${D}${datadir}/tegraflash/
    fi
}

do_install:append:tegra234() {
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${EMMC_BCT}
    install -m 0644 ${B}/slot_metadata.bin ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/tegra234-*.dtsi ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra234-bpmp-*.dtb ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra234* ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/xusb_sil_rel_fw ${D}${datadir}/tegraflash/
    if [ -n "${CBOOTOPTION_FILE}" ]; then
        install -m 0644 ${B}/cbo.dtb ${D}${datadir}/tegraflash/
    fi
    # Copy each dtbo file
    if [ -n "${OVERLAY_DTB_FILE}" ]; then
        dtbo_files=$(echo "${OVERLAY_DTB_FILE}" | sed -e "s/,/ /g")
        for dtbo_file in ${dtbo_files}
        do
            install -m 0644 ${S}/kernel/dtb/${dtbo_file} ${D}${datadir}/tegraflash/
        done
    fi
}

PACKAGES = "${PN}-dev"
FILES:${PN}-dev = "${datadir}"
RRECOMMENDS:${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"