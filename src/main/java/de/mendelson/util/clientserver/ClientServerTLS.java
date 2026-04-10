package de.mendelson.util.clientserver;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Minimal stub interface for ClientServerTLS compatibility.
 * Mina networking removed.
 *
 * @author Julian Xu
 * @version 1.0
 */
public interface ClientServerTLS {
    javax.net.ssl.SSLContext createSSLContext() throws Exception;
    String getProductName();
}
