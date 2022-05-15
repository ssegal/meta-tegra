DESCRIPTION = "L4T bootloader update support tools"
L4T_DEB_COPYRIGHT_MD5 = "fe65a18c0cbf70d19e3d6202a8c825b6"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-tools"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "25a6e3a394d879edaee76de33b90d6d6b24219e212a5024c2c9fdbeb1b67603d"
MAINSUM_tegra210 = "c5ad8c7c1e8508e37aa33bd990840b320c6261fc542cd9cbae5834058b2a7122"

SRC_URI_append_tegra210 = " file://Convert-l4t_payload_updater_t210-to-Python3-R32.7.1.patch"

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/nvbootctrl ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/nv_bootloader_payload_updater ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/nv_update_engine ${D}${sbindir}
	install -d ${D}/opt/ota_package
}

do_install_tegra210() {
	install -d ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/l4t_payload_updater_t210 ${D}${sbindir}
	install -d ${D}/opt/ota_package
}

PACKAGES = "tegra-redundant-boot-nvbootctrl ${PN} ${PN}-dev"
FILES_tegra-redundant-boot-nvbootctrl = "${sbindir}/nvbootctrl"
FILES_${PN} += "/opt/ota_package"
RDEPENDS_${PN} = "tegra-redundant-boot-nvbootctrl setup-nv-boot-control-service tegra-configs-bootloader"
RDEPENDS_${PN}_tegra210 = "setup-nv-boot-control-service python3-core"
INSANE_SKIP_${PN} = "ldflags"
RDEPENDS_tegra-redundant-boot-nvbootctrl = "setup-nv-boot-control"
RDEPENDS_tegra-redundant-boot-nvbootctrl_tegra210 = ""
ALLOW_EMPTY_tegra-redundant-boot-nvbootctrl_tegra210 = "1"
INSANE_SKIP_tegra-redundant-boot-nvbootctrl = "ldflags"