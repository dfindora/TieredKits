//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.shortcircuit.utils;

import javax.annotation.Nonnull;

public class Version implements Comparable<Version>
{
    private Integer major;
    private Integer minor;
    private Integer build;
    private Version sub_version;
    private boolean snapshot;

    public Version(int major)
    {
        this(major, false);
    }

    public Version(int major, Version sub_version)
    {
        this(major, sub_version, false);
    }

    public Version(int major, boolean snapshot)
    {
        this(major, null, snapshot);
    }

    public Version(int major, Version sub_version, boolean snapshot)
    {
        this.major = null;
        this.minor = null;
        this.build = null;
        this.sub_version = null;
        this.snapshot = false;
        this.major = major;
        this.sub_version = sub_version;
        this.snapshot = snapshot;
    }

    public Version(int major, int minor)
    {
        this(major, minor, null, false);
    }

    public Version(int major, int minor, Version sub_version)
    {
        this(major, minor, sub_version, false);
    }

    public Version(int major, int minor, boolean snapshot)
    {
        this(major, minor, null, snapshot);
    }

    public Version(int major, int minor, Version sub_version, boolean snapshot)
    {
        this.major = null;
        this.minor = null;
        this.build = null;
        this.sub_version = null;
        this.snapshot = false;
        this.major = major;
        this.minor = minor;
        this.sub_version = sub_version;
        this.snapshot = snapshot;
    }

    public Version(int major, int minor, int build)
    {
        this(major, minor, build, (Version) null, false);
    }

    public Version(int major, int minor, int build, Version sub_version)
    {
        this(major, minor, build, sub_version, false);
    }

    public Version(int major, int minor, int build, boolean snapshot)
    {
        this(major, minor, build, null, snapshot);
    }

    public Version(int major, int minor, int build, Version sub_version, boolean snapshot)
    {
        this.major = null;
        this.minor = null;
        this.build = null;
        this.sub_version = null;
        this.snapshot = false;
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.sub_version = sub_version;
        this.snapshot = snapshot;
    }

    public Version(String version) throws NumberFormatException
    {
        this.major = null;
        this.minor = null;
        this.build = null;
        this.sub_version = null;
        this.snapshot = false;
        if (version != null)
        {
            String[] version_parts = version.toLowerCase().split("-");
            if (version_parts.length >= 2)
            {
                for (int i = 1; i < version_parts.length; ++i)
                {
                    String part = version_parts[i];
                    if (part.toLowerCase().matches("r\\d(\\.\\d)*"))
                    {
                        this.sub_version = new Version(part.substring(1));
                    }
                    else if (part.equalsIgnoreCase("snapshot"))
                    {
                        this.snapshot = true;
                    }
                }
            }

            String[] parts = version_parts[0].split("\\.");
            if (parts.length >= 3)
            {
                this.build = Integer.parseInt(parts[2]);
            }

            if (parts.length >= 2)
            {
                this.minor = Integer.parseInt(parts[1]);
            }

            if (parts.length >= 1)
            {
                this.major = Integer.parseInt(parts[0]);
            }

        }
    }

    public int getMajor()
    {
        return this.major == null ? 0 : this.major;
    }

    public int getMinor()
    {
        return this.minor == null ? 0 : this.minor;
    }

    public int getBuild()
    {
        return this.build == null ? 0 : this.build;
    }

    public Version getSubVersion()
    {
        return this.sub_version;
    }

    public boolean isSnapshot()
    {
        return this.snapshot;
    }

    public String toString()
    {
        return (this.major == null ? "" : this.major) + (this.minor == null ? "" : (this.major == null ? "" : ".")
                                                                                   + this.minor) + (this.build == null
                                                                                                    ? "" :
                                                                                                    (this.minor == null
                                                                                                     ? "" : ".")
                                                                                                    + this.build) + (
                       this.snapshot ? "-SNAPSHOT" : "") + (this.sub_version == null ? "" : "-R" + this.sub_version
                .toString());
    }

    public boolean equals(Object o)
    {
        return o instanceof Version && ((Version) o).major.equals(this.major)
               && ((Version) o).minor.equals(this.minor) && ((Version) o).build.equals(this.build)
               && ((Version) o).snapshot == this.snapshot && (
                       ((Version) o).sub_version != null && this.sub_version != null && ((Version) o).sub_version
                               .equals(this.sub_version)
                       || ((Version) o).sub_version == null && this.sub_version == null);
    }

    public int compareTo(@Nonnull Version o)
    {
        if (o.getMajor() != this.getMajor())
        {
            return this.getMajor() - o.getMajor();
        }
        else if (o.getMinor() != this.getMinor())
        {
            return this.getMinor() - o.getMinor();
        }
        else if (o.getBuild() != this.getBuild())
        {
            return this.getBuild() - o.getBuild();
        }
        else if (o.isSnapshot() != this.isSnapshot())
        {
            return this.isSnapshot() ? -1 : 1;
        }
        else if (o.sub_version == null && this.sub_version != null)
        {
            return -1;
        }
        else if (o.sub_version != null && this.sub_version == null)
        {
            return 1;
        }
        else
        {
            return o.sub_version != null ? o.sub_version.compareTo(this.sub_version) : 0;
        }
    }
}
