package com.doruk.infrastructure.mail;

public class Templates {

    private static final String CSS_STYLE = """
        <style>
            body { font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #f3f4f6; margin: 0; padding: 0; }
            .wrapper { width: 100%; table-layout: fixed; background-color: #f3f4f6; padding-bottom: 40px; }
            .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; margin-top: 40px; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1); }
            .header { background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%); padding: 40px 20px; text-align: center; }
            .header h1 { color: #ffffff; margin: 0; font-size: 28px; font-weight: 800; letter-spacing: -1px; }
            .content { padding: 40px; color: #374151; }
            .content h2 { color: #111827; font-size: 22px; font-weight: 700; margin-bottom: 16px; }
            .text-main { font-size: 16px; line-height: 24px; margin-bottom: 24px; color: #4b5563; }
        
            /* OTP Section */
            .otp-label { font-size: 12px; font-weight: 700; color: #9ca3af; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 8px; text-align: center; }
            .otp-box { background-color: #f8fafc; border: 2px solid #e2e8f0; border-radius: 12px; padding: 20px; text-align: center; margin-bottom: 30px; }
            .otp-code { font-family: 'Courier New', monospace; font-size: 36px; font-weight: 800; letter-spacing: 10px; color: #4f46e5; margin: 0; }

            /* Button Section */
            .btn-wrapper { text-align: center; margin-bottom: 30px; }
            .btn { background-color: #4f46e5; color: #ffffff !important; padding: 16px 32px; font-weight: 600; text-decoration: none; border-radius: 8px; display: inline-block; transition: background-color 0.2s; }

            /* Fallback Link */
            .fallback-section { background-color: #f9fafb; padding: 20px; border-radius: 8px; border: 1px solid #f3f4f6; }
            .fallback-text { font-size: 12px; color: #6b7280; margin-bottom: 8px; }
            .fallback-link { font-size: 12px; color: #6366f1; word-break: break-all; text-decoration: underline; }

            .footer { padding: 30px; text-align: center; font-size: 13px; color: #9ca3af; }
            @media only screen and (max-width: 600px) { .container { margin-top: 0; border-radius: 0; } }
        </style>
        """;

    public static String emailVerificationTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>""" + CSS_STYLE + """
            </head>
            <body>
                <div class="wrapper">
                    <div class="container">
                        <div class="header"><h1>YakshaSoft</h1></div>
                        <div class="content">
                            <h2>Verify your email address</h2>
                            <p class="text-main">Hey there,<br>Welcome to the community! To finalize your account setup and start exploring, please verify your email address using the code below:</p>

                            <div class="otp-label">Verification Code</div>
                            <div class="otp-box">
                                <p class="otp-code">{{params.otp}}</p>
                            </div>

                            <div class="btn-wrapper">
                                <a href="{{params.url}}" class="btn">Verify Account</a>
                            </div>

                            <div class="fallback-section">
                                <p class="fallback-text">Having trouble with the button? Copy and paste this link into your browser:</p>
                                <a href="{{params.url}}" class="fallback-link">{{params.url}}</a>
                            </div>

                            <p class="text-main" style="margin-top:30px;">Welcome aboard,<br><strong>The YakshaSoft Team</strong></p>
                        </div>
                        <div class="footer">
                            &copy; 2025 YakshaSoft Inc. All rights reserved.<br>
                            You received this email because you registered at yakshasoft.com
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    public static String passwordResetTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>""" + CSS_STYLE + """
            </head>
            <body>
                <div class="wrapper">
                    <div class="container">
                        <div class="header"><h1>YakshaSoft</h1></div>
                        <div class="content">
                            <h2>Password Reset Request</h2>
                            <p class="text-main">Hey there,<br>We received a request to reset your password. If this was you, please enter the code below in the app or click the button to continue:</p>

                            <div class="otp-label">Password Reset Code</div>
                            <div class="otp-box">
                                <p class="otp-code">{{params.otp}}</p>
                            </div>

                            <div class="btn-wrapper">
                                <a href="{{params.url}}" class="btn">Reset Password</a>
                            </div>

                            <div class="fallback-section">
                                <p class="fallback-text">If the button above does not work, use the following secure link:</p>
                                <a href="{{params.url}}" class="fallback-link">{{params.url}}</a>
                            </div>

                            <p class="text-main" style="margin-top:30px; font-size: 13px; color: #9ca3af;">
                                <em>If you did not request a password reset, please ignore this email or contact support if you have concerns.</em>
                            </p>
                        </div>
                        <div class="footer">
                            &copy; 2025 YakshaSoft Inc. &bull; Secure Password Management
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    public static String mfaTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>""" + CSS_STYLE + """
            </head>
            <body>
                <div class="wrapper">
                    <div class="container">
                        <div class="header"><h1>YakshaSoft</h1></div>
                        <div class="content" style="text-align: center;">
                            <h2>Identity Verification</h2>
                            <p class="text-main">Dear User,<br>Please use the code below to complete your sign-in process. This code will expire shortly.</p>

                            <div class="otp-label">MFA Code</div>
                            <div class="otp-box">
                                <p class="otp-code">{{params.otp}}</p>
                            </div>

                            <p style="color: #ef4444; font-size: 14px; font-weight: 600;">Never share this code with anyone.</p>
                        </div>
                        <div class="footer">
                            Secured by YakshaSoft Guard
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    public static String genericTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>""" + CSS_STYLE + """
            </head>
            <body>
                <div class="wrapper">
                    <div class="container">
                        <div class="header"><h1>YakshaSoft</h1></div>
                        <div class="content" style="text-align: center;">
                            <h2>Identity Verification</h2>
                            <p class="text-main">Use the code below to complete your action. This code will expire shortly.</p>

                            <div class="otp-label">MFA Code</div>
                            <div class="otp-box">
                                <p class="otp-code">{{params.otp}}</p>
                            </div>

                            <p style="color: #ef4444; font-size: 14px; font-weight: 600;">Never share this code with anyone.</p>
                        </div>
                        <div class="footer">
                            Secured by YakshaSoft Guard
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}