/*
 *  This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 *  Licensed to the Internet Archive (IA) by one or more individual 
 *  contributors. 
 *
 *  The IA licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.archive.modules.extractor;


/**
 * The kind of "hop" from one URI to another.  Each hop type can be 
 * represented by a single character; strings of these characters can 
 * appear in logs.  Eg, "LLLX" means that a URI was three normal links from
 * a seed, and then one speculative link.
 * 
 * @author pjack
 */
public enum Hop {

    /**
     * 一般的<a>链接 
     * Navigation links, like A/@HREF. 
     * */
    NAVLINK('L'),
    
    /** 
     * 隐含的必要的链接
     * Implied prerequisite links, like dns or robots. 
     * */
    PREREQ('P'),
    
    /**
     * 内嵌的必要的用来渲染页面的链接，比如图片链接 
     * Embedded links necessary to render the page, like IMG/@SRC. 
     * */
    EMBED('E'),
    
    /** 
     * 内嵌在JavaScript中的链接
     * Speculative/aggressively extracted links, perhaps embed or nav, 
     * as in javascript.  
     */
    SPECULATIVE('X'),
    
    /** 
     * 重定向链接
     * Referral/redirect links, like header 'Location:' on a 301/302 response.
     * */
    REFER('R'),

    /** 
     * 隐藏的链接，根据规则推断的
     * Inferred/implied links -- not necessarily literally in the source 
     * material, but deduced by convention.
     * */
    INFERRED('I'),
    
    /** 
     * form表单提交链接
     * Synthesized form-submit 
     * */ 
    SUBMIT('S');
    
    /** The hop character for logs. */
    private char hopChar;
    protected String hopString; 
    
    /**
     * Constructor.
     * 
     * @param hopChar  the hop character for logs
     */
    private Hop(char hopChar) {
        this.hopChar = hopChar;
        this.hopString = ""+hopChar;
    }

    
    /**
     * Returns a hop character suitable for display in logs.
     * 
     * @return   the hop character
     */
    public char getHopChar() {
        return hopChar;
    }


    public String getHopString() {
        return hopString;
    }
}
