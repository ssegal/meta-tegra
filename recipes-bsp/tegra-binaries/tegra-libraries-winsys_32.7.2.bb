L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "97fd11faa79ab16f5525492628108a68a58a5c1c5127ba58509784d58b90729c"
MAINSUM_tegra210 = "29fa00ca54f7776503a608989ae7baa2459cc1a1d52e075819a0a07761192c25"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvwinsys.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"